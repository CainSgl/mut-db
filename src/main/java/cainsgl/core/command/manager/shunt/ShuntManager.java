package cainsgl.core.command.manager.shunt;


import io.netty.util.concurrent.Future;

public interface ShuntManager<D>
{

    void create(D... datas);


    Future<D> separate();

    void setTester(Tester tester);

    Future<Integer> overLoad();


    Future<D> destory();

    void exceptionCaught(Exception cause);
}
