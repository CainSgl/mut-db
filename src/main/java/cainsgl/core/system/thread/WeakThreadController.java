package cainsgl.core.system.thread;

import io.netty.channel.DefaultEventLoop;
import io.netty.channel.EventLoop;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.util.concurrent.DefaultThreadFactory;

import java.lang.ref.Reference;
import java.lang.ref.ReferenceQueue;
import java.lang.ref.WeakReference;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

public class WeakThreadController implements ThreadController {
    // 建议的最大线程配额
    private final int suggestedMaxThreads;
    // 当前通过弱引用跟踪的线程数（仅作统计）
    private final AtomicInteger trackedThreads = new AtomicInteger(0);

    // 弱引用队列（检测GC回收的线程）
    private final ReferenceQueue<EventLoop> eventLoopRefQueue = new ReferenceQueue<>();
    // 活动线程的弱引用集合（同步集合）
    private final Set<WeakReference<EventLoop>> activeEventLoops =
            Collections.synchronizedSet(new HashSet<>());

    public WeakThreadController(int suggestedMaxThreads) {
        this.suggestedMaxThreads = Math.max(suggestedMaxThreads, 1);
    }

    @Override
    public synchronized EventLoop getEventLoop() {
        // 1. 清理已被GC回收的弱引用（释放资源）
        cleanUpRefQueue();

        // 2. 尝试从弱引用集合中获取可复用的线程
        EventLoop reusableLoop = findReusableLoop();
        if (reusableLoop != null) {
            return reusableLoop;
        }

        // 3. 无可用复用时，直接创建新线程（允许超过建议配额）
        EventLoop newLoop = createNewEventLoop();
        activeEventLoops.add(new WeakReference<>(newLoop, eventLoopRefQueue));
        trackedThreads.incrementAndGet();
        return newLoop;
    }

    @Override
    public synchronized void backEventLoop(EventLoop eventLoop) {
        // 主动移除强引用（触发弱引用生效）
        activeEventLoops.removeIf(ref -> ref.get() == eventLoop);
        // 立即清理可能已失效的引用
        cleanUpRefQueue();
    }

    @Override
    public boolean hasMoreThreads() {
        // 清理后检查当前跟踪的线程数是否小于建议配额
        cleanUpRefQueue();
        return trackedThreads.get() < suggestedMaxThreads;
    }

    // 私有方法：清理引用队列中的失效引用
    private void cleanUpRefQueue() {
        Reference<? extends EventLoop> ref;
        while ((ref = eventLoopRefQueue.poll()) != null) {
            activeEventLoops.remove(ref);  // 从活动集合中移除失效引用
            EventLoop loop = ref.get();
            if (loop != null && !loop.isShuttingDown()) {
                loop.shutdownGracefully();  // 确保资源释放
            }
            trackedThreads.decrementAndGet();  // 调整跟踪计数
        }
    }

    // 私有方法：查找可复用的有效线程
    private EventLoop findReusableLoop() {
        Iterator<WeakReference<EventLoop>> iterator = activeEventLoops.iterator();
        while (iterator.hasNext()) {
            WeakReference<EventLoop> ref = iterator.next();
            EventLoop loop = ref.get();

            // 移除已失效的引用（避免集合膨胀）
            if (loop == null) {
                iterator.remove();
                trackedThreads.decrementAndGet();
                continue;
            }

            // 检查线程是否有效（未关闭且可用）
            if (!loop.isShuttingDown() && !loop.isTerminated()) {
                return loop;  // 返回第一个可用的线程
            } else {
                // 无效线程：主动关闭并移除引用
                loop.shutdownGracefully();
                iterator.remove();
                trackedThreads.decrementAndGet();
            }
        }
        return null;  // 无可用线程
    }

    // 私有方法：创建新的EventLoop
    private EventLoop createNewEventLoop() {
        return new DefaultEventLoop(
                new DefaultThreadFactory("DynamicEventLoop-" + trackedThreads.get())
        );
    }
    @Override
    public synchronized EventLoopGroup getEventLoopGroup(int threadsNum) {
        cleanUpGroupRefQueue();
        EventLoopGroup reusableGroup = findReusableGroup();
        if (reusableGroup != null) {
            return reusableGroup;
        }
        EventLoopGroup newGroup = new NioEventLoopGroup(threadsNum,
                new DefaultThreadFactory("DynamicGroup-" + trackedThreads.get()));
        activeGroups.add(new WeakReference<>(newGroup, groupRefQueue));
        trackedThreads.addAndGet(threadsNum);
        return newGroup;
    }

    @Override
    public synchronized void backLoopGroup(EventLoopGroup group) {
        activeGroups.removeIf(ref -> ref.get() == group);
        cleanUpGroupRefQueue();
    }

    // 线程组相关的弱引用管理（简化示意）
    private final ReferenceQueue<EventLoopGroup> groupRefQueue = new ReferenceQueue<>();
    private final Set<WeakReference<EventLoopGroup>> activeGroups =
            Collections.synchronizedSet(new HashSet<>());

    private void cleanUpGroupRefQueue() {
        Reference<? extends EventLoopGroup> ref;
        while ((ref = groupRefQueue.poll()) != null) {
            activeGroups.remove(ref);
            EventLoopGroup group = ref.get();
            if (group != null && !group.isShuttingDown()) {
                group.shutdownGracefully();
            }
            trackedThreads.addAndGet(-((NioEventLoopGroup) group).executorCount());
        }
    }

    private EventLoopGroup findReusableGroup() {
        Iterator<WeakReference<EventLoopGroup>> iterator = activeGroups.iterator();
        while (iterator.hasNext()) {
            WeakReference<EventLoopGroup> ref = iterator.next();
            EventLoopGroup group = ref.get();
            if (group == null) {
                iterator.remove();
                continue;
            }
            if (!group.isShuttingDown() && !group.isTerminated()) {
                return group;
            } else {
                group.shutdownGracefully();
                iterator.remove();
            }
        }
        return null;
    }
}