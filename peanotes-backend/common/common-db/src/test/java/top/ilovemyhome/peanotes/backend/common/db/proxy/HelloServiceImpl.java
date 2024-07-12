package top.ilovemyhome.peanotes.backend.common.db.proxy;

public class HelloServiceImpl implements HelloService {
    @Override
    public void sayHello(String name) {
        if (name == null) {
            throw new IllegalArgumentException("name can not be null");
        }
        System.out.println("Hello " + name);
    }
}
