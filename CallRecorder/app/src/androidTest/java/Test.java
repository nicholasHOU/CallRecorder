public class Test {

    public static void main(String[] args) {
        B a = new B();
        if (a instanceof ListenerA){
            ((ListenerA)a).convert();
        }else {
            System.out.println("AAAAA");
        }
    }

    static class A implements ListenerA{

        @Override
        public void convert() {
            System.out.println("A");
        }
    }
    static class B implements ListenerA{

        @Override
        public void convert() {
            System.out.println("B");

        }
    }
    static class C implements ListenerA{

        @Override
        public void convert() {
            System.out.println("C");

        }
    }
    static interface ListenerA {
        void convert();
    }
}
