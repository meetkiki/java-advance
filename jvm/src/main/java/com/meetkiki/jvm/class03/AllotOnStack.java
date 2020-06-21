package com.meetkiki.jvm.class03;


import com.meetkiki.jvm.User;

/**
	* 栈上分配，标量替换
	* 代码调用了1亿次alloc()，如果是分配到堆上，大概需要1GB以上堆空间，如果堆空间小于该值，必然会触发GC。
*
	* 使用如下参数不会发生GC
	* -Xmx15m -Xms15m -XX:+DoEscapeAnalysis -XX:+PrintGC
	* 使用如下参数都会发生大量GC
	* -Xmx15m -Xms15m -XX:-DoEscapeAnalysis -XX:+PrintGC
 */
 public class AllotOnStack {

    public static void main(String[] args) {
        long start = System.currentTimeMillis();
        for (int i = 0; i < 1000000000; i++) {
            alloc();
        }
        long end = System.currentTimeMillis();
        System.out.println(end - start);
    }

    private static void alloc() {
        User user = new User();
        user.setId(1);
        user.setName("zhuge");
    }
}