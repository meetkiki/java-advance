package com.meetkiki.jvm.lesson01;

import java.io.FileInputStream;
import java.lang.reflect.Method;

public class MyClassLoaderTest {

    private static final String PACKAGE = MyClassLoaderTest.class.getPackage().getName();

    static class MyClassLoader extends ClassLoader {
        private String classPath;

        public MyClassLoader(String classPath) {
            this.classPath = classPath;
        }

        private byte[] loadByte(String name) throws Exception {
            name = name.replaceAll("\\.", "/");
            try (FileInputStream fis = new FileInputStream(classPath + "/" + name + ".class")) {
                byte[] data;
                int len = fis.available();
                data = new byte[len];
                fis.read(data);
                return data;
            }
        }

        protected Class<?> findClass(String name) throws ClassNotFoundException {
            try {
                byte[] data = loadByte(name);
                //defineClass将一个字节数组转为Class对象，这个字节数组是class文件读取后最终的字节数组。
                return defineClass(name, data, 0, data.length);
            } catch (Exception e) {
                e.printStackTrace();
                throw new ClassNotFoundException();
            }
        }

        protected Class<?> loadClass(String name, boolean resolve)
                throws ClassNotFoundException {
            synchronized (getClassLoadingLock(name)) {
                // First, check if the class has already been loaded
                Class<?> c = findLoadedClass(name);
                if (c == null) {
                    long t0 = System.nanoTime();

                    if (name.startsWith(PACKAGE)) {
                        c = findClass(name);
                    } else {
                        c = this.getParent().loadClass(name);
                    }

                    if (c == null) {
                        // If still not found, then invoke findClass in order
                        // to find the class.
                        long t1 = System.nanoTime();
                        c = findClass(name);

                        // this is the defining class loader; record the stats
                        sun.misc.PerfCounter.getParentDelegationTime().addTime(t1 - t0);
                        sun.misc.PerfCounter.getFindClassTime().addElapsedTimeFrom(t1);
                        sun.misc.PerfCounter.getFindClasses().increment();
                    }
                }
                if (resolve) {
                    resolveClass(c);
                }
                return c;
            }
        }

    }


    public static void main(String args[]) throws Exception {
        //初始化自定义类加载器，会先初始化父类ClassLoader，其中会把自定义类加载器的父加载器设置为应用程序类加载器AppClassLoader
        MyClassLoader classLoader = new MyClassLoader("D:\\workspace\\java-advanced\\jvm\\target\\classes");
        //D盘创建 test/com/tuling/com.meetkiki.jvm 几级目录，将User类的复制类User1.class丢入该目录
        Class<?> clazz = classLoader.loadClass("com.meetkiki.jvm.com.meetkiki.jvm.User");
        Object obj = clazz.newInstance();
        Method method = clazz.getDeclaredMethod("sout");
        method.invoke(obj);
        System.out.println(clazz.getClassLoader().getClass().getName());
    }
}

