package cainsgl.core.network.response.impl;

import cainsgl.core.network.response.ElementResponse;
import cainsgl.core.utils.RespUtils;

import java.util.ArrayList;
import java.util.List;

public class LazyArrayResponse implements ElementResponse
{
    private final List<ElementResponse> elementsList;  // 使用List存储元素
    private byte[] data;  // 延迟生成的字节数组（缓存）
    private int len;      // 延迟计算的总长度（缓存）
    private static final byte separator = '*';

    // 构造函数：从数组初始化
    public LazyArrayResponse(ElementResponse... elements) {
        this.elementsList = new ArrayList<>();
        if (elements != null) {
            for (ElementResponse element : elements) {
                elementsList.add(element);
            }
        }
    }

    // 构造函数：从列表初始化
    public LazyArrayResponse(List<ElementResponse> elements) {
        this.elementsList = new ArrayList<>(elements);  // 创建副本保证内部列表独立
    }

    // 动态添加元素方法
    public void addElement(ElementResponse element) {
        elementsList.add(element);
        invalidateCache();  // 修改后使缓存失效
    }

    // 动态移除元素方法（按索引）
    public ElementResponse removeElement(int index) {
        if (index < 0 || index >= elementsList.size()) {
            throw new IndexOutOfBoundsException("Index: " + index + ", Size: " + elementsList.size());
        }
        ElementResponse removed = elementsList.remove(index);
        invalidateCache();  // 修改后使缓存失效
        return removed;
    }

    // 清空所有元素
    public void clearElements() {
        elementsList.clear();
        invalidateCache();  // 修改后使缓存失效
    }

    // 获取当前元素数量
    public int elementCount() {
        return elementsList.size();
    }

    // 使缓存失效（当元素列表变化时调用）
    private void invalidateCache() {
        data = null;  // 清除字节数组缓存
        len = 0;      // 重置长度缓存
    }

    private int writeHeader(int elementCount, byte[] dest, int offset) {
        dest[offset++] = separator;
        offset = RespUtils.writeIntAsAscii(elementCount, dest, offset);
        dest[offset++] = '\r';
        dest[offset++] = '\n';
        return offset;
    }

    @Override
    public int len() {
        if (data == null) {  // 未生成则触发生成
            getBytes();
        }
        return len;
    }

    @Override
    public int writeByte(byte[] buf, int off) {
        byte[] data = getBytes();  // 确保数据已生成
        System.arraycopy(data, 0, buf, off, len);
        return off + len;
    }

    @Override
    public byte[] getBytes() {
        if (data != null) {  // 使用缓存
            return data;
        }

        int elementCount = elementsList.size();
        // 计算总头长度（分隔符* + 元素数量的ASCII长度 + \r\n）
        int totalHeaderLen = 1 + RespUtils.getDigitCount(elementCount) + 2;
        // 计算所有子元素总长度
        int totalElementLen = 0;
        for (ElementResponse element : elementsList) {
            totalElementLen += element.len();
        }

        // 初始化字节数组并填充数据
        data = new byte[totalHeaderLen + totalElementLen];
        int offset = writeHeader(elementCount, data, 0);
        for (ElementResponse element : elementsList) {
            offset = element.writeByte(data, offset);  // 写入子元素数据
        }
        len = offset;  // 记录总长度

        return data;
    }
}
