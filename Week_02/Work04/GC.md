### 1、测试环境

#### 1.1 电脑配置

- 系统：Windows 10
- CUP：i5-8350H
- 内存：8G
- JDK：1.8.0_111

#### 1.2 测试代码

```java
import java.util.Random;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.LongAdder;
/*
演示GC日志生成与解读
*/
public class GCLogAnalysis {
    private static Random random = new Random();
    public static void main(String[] args) {
        // 当前毫秒时间戳
        long startMillis = System.currentTimeMillis();
        // 持续运行毫秒数; 可根据需要进行修改
        long timeoutMillis = TimeUnit.SECONDS.toMillis(1);
        // 结束时间戳
        long endMillis = startMillis + timeoutMillis;
        LongAdder counter = new LongAdder();
        System.out.println("正在执行...");
        // 缓存一部分对象; 进入老年代
        int cacheSize = 2000;
        Object[] cachedGarbage = new Object[cacheSize];
        // 在此时间范围内,持续循环
        while (System.currentTimeMillis() < endMillis) {
            // 生成垃圾对象
            Object garbage = generateGarbage(100*1024);
            counter.increment();
            int randomIndex = random.nextInt(2 * cacheSize);
            if (randomIndex < cacheSize) {
                cachedGarbage[randomIndex] = garbage;
            }
        }
        System.out.println("执行结束!共生成对象次数:" + counter.longValue());
    }

    // 生成对象
    private static Object generateGarbage(int max) {
        int randomSize = random.nextInt(max);
        int type = randomSize % 4;
        Object result = null;
        switch (type) {
            case 0:
                result = new int[randomSize];
                break;
            case 1:
                result = new byte[randomSize];
                break;
            case 2:
                result = new double[randomSize];
                break;
            default:
                StringBuilder builder = new StringBuilder();
                String randomString = "randomString-Anything";
                while (builder.length() < randomSize) {
                    builder.append(randomString);
                    builder.append(max);
                    builder.append(randomSize);
                }
                result = builder.toString();
                break;
        }
        return result;
    }
}
```



### 2、SerialGC(串行GC)

单线程GC，进行垃圾收集时触发 STW 事件，必须暂停其他所有工作线程，直到它垃圾回收结束。

新生代使用 Serial 收集器，老年代使用 Serial Old 收集器。新生代中采用的是标记-复制算法，老年代中采用的是标记-整理算法。

**执行命令：**

```shell
java -XX:+UseSerialGC -Xms512m -Xmx512m [-Xloggc:gc.demo.log] -XX:+PrintGCDetails -XX:+PrintGCDateStamps GCLogAnalysis
```

**不同-Xms和-Xmx配置，执行结果**：

|         配置          | 生成对象次数 | Yong GC次数(5次均值) | Young GC时间(毫秒) | Full GC次数(5次均值) | Full GC时间(毫秒) |
| :-------------------: | :----------: | :------------------: | :----------------: | :------------------: | :---------------: |
|   -Xms512m -Xmx512m   | 10000~13000  |          18          |        5~35        |          5           |       30~40       |
|     -Xms2g -Xmx2g     | 17000~19000  |          9           |       40~52        |          0           |                   |
|     -Xms4g -Xmx4g     | 16000~17000  |          4           |                    |          0           |                   |
|     -Xms8g -Xmx8g     | 16000~17000  |          2           |                    |          0           |                   |
|    -Xms16g -Xmx16g    | 12000~14000  |          0           |                    |          0           |                   |
| -Xms512g -Xmx8g(默认) | 16000~17000  |          2           |       10~20        |          0           |                   |

**结论：**

1. 堆内存越大，运行效率越高，GC次数越少，每次GC花费的时间越多，**但是**，当堆内存增加到一定程度时，运行效率却越来越低。
2. 当xmx相同时(均为默认8G)，xms越小，第一次发生GC时间越早

### 3、ParallelGC(并行GC)

新生代中有 Parallel New 和 Parallel Scavenge ，老年代中是 Parallel Old，相当于 Serial GC 的多线程版。新生代中采用的是标记-复制算法，老年代中采用的是标记-整理算法，在垃圾回收时也会触发 STW 事件。

Parallel Scavenge收集器的目标是达到一个可控件的吞吐量，所谓吞吐量就是CPU用于运行用户代码的时间与CPU总消耗时间的比值，即**吞吐量 = 运行用户代码时间 / （运行用户代码时间 + 垃圾收集时间）**。如果虚拟机总共运行了100分钟，其中垃圾收集花了1分钟，那么吞吐量就是99%，所以又被称为吞吐量优先收集器。

并行 GC 默认的线程数为当前 CPU 的核数，可以通过 -XX:ParallelGCThreads=N 来指定 GC 线程数。

在使用 -XX:+useParalelGC 的时候新生代使用的是 Parallel Scavenge GC。

**执行命令：**

```shell
java -XX:+UseParallelGC -Xms512m -Xmx512m [-Xloggc:gc.demo.log] -XX:+PrintGCDetails -XX:+PrintGCDateStamps GCLogAnalysis
```

**不同-Xms和-Xmx配置，执行结果**：

|         配置          | 生成对象次数 | Yong GC次数(5次均值) | Young GC时间(毫秒) | Full GC次数(5次均值) | Full GC时间(毫秒) |
| :-------------------: | :----------: | :------------------: | :----------------: | :------------------: | :---------------: |
|   -Xms512m -Xmx512m   |  9000~11000  |          25          |        2~10        |          18          |       28~33       |
|     -Xms2g -Xmx2g     | 20000~22000  |          17          |       14~23        |          0           |                   |
|     -Xms4g -Xmx4g     | 22000~24000  |          4           |       25~32        |          0           |                   |
|     -Xms8g -Xmx8g     | 16000~20000  |          2           |       33~40        |          0           |                   |
|    -Xms16g -Xmx16g    | 12000~14000  |          0           |                    |          0           |                   |
| -Xms512g -Xmx8g(默认) | 21000~23000  |          9           |        5~37        |          1           |       30~33       |

**结论：**

1. 堆内存越大，运行效率越高，GC次数越少，GC花费的时间越多，但是，当堆内存增加到一定程度时，运行效率却越来越低。
2. 当xmx相同时(均为默认8G)，xms越小，第一次发生GC时间越早
3. Young GC只清理Young区(清空为0)，Old区不清理，Full GC Young区清空为0，Old清除不活跃的数据

### 3、CMS GC(Concurrent Mark-Sweep GC)

CMS 是一种以获取最短回收停顿时间为目标的收集器，采用的是标记-清除算法。

**阶段：**

- 阶段 1：Initial Mark（初始标记，时间很短，只标记根对象及根对象直接引用的对象）
- 阶段 2：Concurrent Mark（并发标记，该阶段与应用程序同时运行， 遍历老年代，从前一个阶段找到的跟对象开始，标记所有的存活对象）
- 阶段 3：Concurrent Preclean（并发预清理，该阶段也是与应用程序并发执行，因为上个阶段是并发执行，可能有些引用已经发生了改变，如果在并发标记过程中引用关系发生了变化，JVM 会通过"Card（卡片）"的方式将发生了改变的区域标记为"脏"区，这就是所谓的卡片标记）
- 阶段 4： Final Remark（最终标记，触发 STW 事件，完成老年代中所有存活的对象的标记）
- 阶段 5： Concurrent Sweep（并发清除，该阶段需要触发 STW 事件，JVM 在此阶段删除不再使用的对象，并回收他们所占用的内存空间）
- 阶段 6： Concurrent Reset（并发重置，该阶段与应用程序并发执行，重置 CMS 算法相关的内部数据，为下一次 GC 循环做准备）

**执行命令：**

```shell
java -XX:+UseConcMarkSweepGC -Xms512m -Xmx512m [-Xloggc:gc.demo.log] -XX:+PrintGCDetails -XX:+PrintGCDateStamps GCLogAnalysis
```

**不同-Xms和-Xmx配置，执行结果：**

|         配置          | 生成对象次数 | Yong GC次数(5次均值) | Young GC时间(毫秒) | CMS GC次数(5次均值) | CMS GC时间(毫秒) |
| :-------------------: | :----------: | :------------------: | :----------------: | :-----------------: | :--------------: |
|   -Xms512m -Xmx512m   | 12000~14000  |          23          |                    |         13          |                  |
|     -Xms2g -Xmx2g     | 18000~22000  |          10          |                    |          1          |                  |
|     -Xms4g -Xmx4g     | 20000~21000  |          8           |                    |          0          |                  |
|     -Xms8g -Xmx8g     | 18000~20000  |          7           |                    |          0          |                  |
|    -Xms16g -Xmx16g    | 17000~19000  |          7           |                    |          0          |                  |
| -Xms512g -Xmx8g(默认) | 15000~16000  |          27          |                    |          1          |                  |

**结论：**

1. 堆内存越大，运行效率越高，GC次数越少，但是，当堆内存增加到一定程度时，CMS GC不再触发，运行效率却越来越低
2. xmx相同情况下(均为默认8G)，xms越大，第一次发生GC时间越晚(xms为512m时Young区大小为139776K)，**但是**，当xms增加到一定程度(1G)，第一次GC时间不再改变(Young区大小一直为681600K)



### 4、Carbage First(G1 GC)

G1最大的特点是引入面向局部收集的设计思路和基于Region的内存布局形式，弱化了分代的概念，合理利用垃圾收集各个周期的资源，解决了其他收集器的众多缺陷。

**阶段：**

- Evacuation Pause: young（纯年轻代模式转移暂停）
- Concurrent Marking（并发标记）
- 阶段 1: Initial Mark（初始标记）
- 阶段 2: Root Region Scan（Root区扫描）
- 阶段 3: Concurrent Mark（并发标记）
- 阶段 4: Remark（再次标记）
- 阶段 5: Cleanup（清理）
- Evacuation Pause (mixed)（转移暂停: 混合模式）
- Full GC (Allocation Failure)

**执行命令：**

```shell
java -XX:+UseG1GC -Xms512m -Xmx512m [-Xloggc:gc.demo.log] -XX:+PrintGC -XX:+PrintGCDateStamps GCLogAnalysis
```

**不同-Xms和-Xmx配置，执行结果：**

|         配置          | 生成对象次数 | Yong GC次数(5次均值) | Young GC时间(毫秒) | G1 GC次数(5次均值) | G1 GC时间(毫秒) |
| :-------------------: | :----------: | :------------------: | :----------------: | :----------------: | :-------------: |
|   -Xms512m -Xmx512m   | 11000~13000  |          43          |                    |         32         |                 |
|     -Xms2g -Xmx2g     | 12000~20000  |          10          |                    |         2          |                 |
|     -Xms4g -Xmx4g     | 18000~23000  |          13          |                    |         0          |                 |
|     -Xms8g -Xmx8g     | 22000~24000  |          14          |                    |         0          |                 |
|    -Xms16g -Xmx16g    | 22000~25000  |          8           |                    |         0          |                 |
| -Xms512g -Xmx8g(默认) | 11000~13000  |          13          |                    |         0          |                 |
|   CMS | 11000~13000  |          13          |                    |         0          |                 |
**结论：**

1. 堆内存越大，运行效率越高，GC次数越少，但是，当堆内存增加到一定程度时，G1 GC不再触发，运行效率变化不大

2. xmx相同情况下(均为默认8G)，Young GC次数基本相同，xms越大，效率越高，第一次发生GC时间越晚

**一次GC也没发生，young区和整个堆的大小一致，因为old区没有使用，整个堆使用的大小就是young区的使用的大小。**

### 5、测试结果

1. -Xms，-XMx默认配置下G1效率最低，ParallelGC效率最高
2. 堆内存越大，运行效率越高，GC次数越少，但是，当堆内存增加到一定程度时，运行效率却越来越低
3. 当xmx相同时(均为默认8G)，xms越小，第一次发生GC时间越早。其中，CMS GC -xms到达一定界限后，第一次发生GC时间不变？？？



### 6、GC对比

**各个 GC 对比**

|         收集器          | 串行、并行or并发 | 新生代/老年代 | 算法 | 目标 | 适用场景 |
| :-------------------: | :----------: | :------------------: | :----------------: | :----------------: | :-------------: |
|   Serial   | 串行  |          新生代          |          复制算法          |         响应速度优先         |        单CPU环境下的Client模式         |
|   Serial Old  | 串行  |          老年代          |         标记-整理           |         响应速度优先          |         单CPU环境下的Clien模式 CMS的后备预案        |
|   ParNew   | 并行  |          新生代          |          复制算法          |         响应速度优先          |         多CPU环境时在Server模式下与CMS配合        |
|   Parallel Scavenge    | 并行  |          新生代          |       复制算法             |         吞吐量优先          |       在后台运算而不需要太多交互的任务          |
|   Parallel Old    | 并行  |          老年代           |          标记-整理          |         吞吐量优先          |        在后台运算而不需要太多交互的任务         |
|   CMS | 并发  |          老年代          |          标记-清除          |         响应速度优先          |        集中在互联网站或B/S系统服务端上的Java应用         |
|   G1 | 并发  |          both          |         标记-整理+复制算法           |         响应速度优先          |        面向服务端应用 将来替换CMS         |

**常用的 GC 组合：**

1. Serial+Serial Old 实现单线程的低延迟垃圾回收机制
2. ParNew+CMS，实现多线程的低延迟垃圾回收机制
3. Parallel Scavenge和ParallelScavenge Old，实现多线程的高吞吐量垃圾回收机制

**GC 如何选择**

**一般性的指导原则：**

1. 如果系统考虑吞吐优先，CPU 资源都用来最大程度处理业务，用 Parallel GC；
2. 如果系统考虑低延迟有限，每次 GC 时间尽量短，用 CMS GC；
3. 如果系统内存堆较大，同时希望整体来看平均 GC 时间可控，使用 G1 GC

**对于内存大小的考量：**

1. 一般 4G 以上，算是比较大，用 G1 的性价比较高。
2. 一般超过 8G，比如 16G-64G 内存，非常推荐使用 G1 GC。