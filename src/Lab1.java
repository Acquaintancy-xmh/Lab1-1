import java.io.*;
import java.util.*;

//this is lab1,add some files
//don't talk anymore
/**
 * @author xmh_mac
 *
 */
interface labInterface {

    /**
     * @param filename
     *            the input of the whole program.
     * @return the result.
     */
    Digraph createDirectedGraph(String filename);

    /**
     * @param graphParam
     *            the input.
     */
    void showDirectedGraph(Digraph graphParam); // 展示有向图 done

    /**
     * @param graphParam
     *            the input.
     * @param word1
     *            the input.
     * @param word2
     *            the input.
     * @return the result.
     */
    String queryBridgeWords(Digraph graphParam, String word1, String word2); // 查询桥接词

    /**
     * @param graphParam
     *            the input.
     * @param inputText
     *            the input.
     * @return String the result.
     */
    String generateNewText(Digraph graphParam, String inputText); // 根据bridge word生成新文本

    /**
     * @param graphParam
     *            the input.
     * @param word1
     *            the input.
     * @param word2
     *            the input.
     * @return String the result.
     */
    String calcShortestPath(Digraph graphParam,
            String word1,
            String word2); // 计算两个单词之间的最短路径

    /**
     * @param graphParam
     *            the input.
     * @return String the result.
     */
    String randomWalk(Digraph graphParam); // 随机游走
}

class Digraph implements labInterface {
    public String[] refrence;// 存放单词，以下标为单词节点编号
    public int[][] list;// 有向图的二维矩阵
    public static int[] ifvisited;// 访问标记数组
    public static int times;// 随机访问时的重复次数，作为全局变量
    public static int sign;// 当前随机到位置
    public static int least;// 最短路径的权值合

    public Digraph(int i)// 定义一个字符串数组
    {
        this.refrence = new String[i];
        this.list = new int[i][i];
    }

    /**
     * @param i the integer.
     */
    public static void refreshIfVisited(final int i)// 刷新访问数组，在使用之前调用一次
    {
        ifvisited = new int[i];
    }

    public static void refreshtimes()// 重置重复次数，需要时调用
    {
        times = 0;
    }

    public static void refreshleast()// 重置最短路径合
    {
        least = 0;
    }

    public int getLength()// 得到数组的有效长度
    {
        for (int i = 0; i < this.refrence.length; i++) {
            if (this.refrence[i] == null)
                return i;
        }
        return 0;
    }

    public boolean IfHaveChild(String str)// 是否存在相邻边，存在返回true
    {
        for (int i = 0; i < this.getLength(); i++) {
            if (this.list[this.GetNum(str)][i] > 0) {
                return true;
            }
        }
        return false;
    }

    public int GetNum(String str)// 通过字符串查找对应的编号值
    {
        for (int j = 0; j < this.getLength(); j++) {
            if (this.refrence[j] == null)
                return -1;
            if (this.refrence[j].equals(str) == true)
                return j;
        }
        return -1;
    }

    public void Add(int i, int j)// 将对应的两个编号的权值+1，表示i->j有一条有向边
    {
        this.list[i][j] = this.list[i][j] + 1;
    }

    public Digraph createDirectedGraph(String filename)// 创建有向图
    {
        try // 存在后进行文件操作
        {
            File file = new File(filename); // 文件打开
            if (!file.exists())// 检验是否存在
            {
                System.err.println("Not Fing the File!\n");
            }

            String line;
            String reg = "\\s+";
            String E = "[a-zA-Z]";
            String word, preword = null;
            int index = 0;

            BufferedReader bf = new BufferedReader(new FileReader(file));
            while ((line = bf.readLine()) != null) // 一行一行读取
            {
                for (int i = 0; i < line.length(); i++) {
                    char ch = line.charAt(i);
                    String ch1 = String.valueOf(ch);
                    if (!ch1.matches(E)) // 将非英文字母字符用空格取代
                        line = line.replace(ch, ' ');

                }
                String[] strs = line.split(reg);
                for (int i = 0; i < strs.length; i++) {
                    word = strs[i];
                    if (this.GetNum(word.toLowerCase().trim()) == -1)// 如果没有储存这个单词，则存入
                    {
                        this.refrence[index] = (word.toLowerCase().trim());
                        index++;
                    } // 有效单词存入参考数组中,同时大写转换

                    if (preword != null)// 如果前一单词不为空，则前一单词到此单词的权值+1
                    {

                        this.Add(this.GetNum(preword.toLowerCase().trim()), this.GetNum(word.toLowerCase().trim()));
                    }
                    preword = word;
                    // System.out.println(preword);
                }
            }
            bf.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return this;
    }

    public void printdigraph(Digraph graphParam)// 测试输出
    {
        /* 输出测试区 */
        int index = graphParam.getLength();
        System.out.println(index);
        for (int z = 0; z < index; z++) {
            System.out.print("\"" + graphParam.refrence[z] + "\"" + ' ');
        }
        System.out.print('\n');
        for (int x = 0; x < index; x++) {
            for (int y = 0; y < index; y++) {
                System.out.print(graphParam.list[x][y] + " ");
            }
            System.out.println("\n");
        }
    }

    public String queryBridgeWords(Digraph graphParam, String word1, String word2)// 查询桥接词
    {
        int index1 = graphParam.GetNum(word1);
        int index2 = graphParam.GetNum(word2);
        if (index1 == -1 || index2 == -1)
            return "No in"; // 至少有一个词不存在
        else if (graphParam.list[index1][index2] > 0)
            return "No have";// 相邻两词无桥接词
        else {
            ifvisited[index1] = 1;
            ifvisited[index2] = 1;
            for (int i = 0; i < graphParam.getLength(); i++)// 判断是否两词间有桥接词
            {
                if (graphParam.list[index1][i] > 0) {
                    if (graphParam.list[i][index2] > 0 && ifvisited[i] == 0)// 有且未被访问过
                    {
                        ifvisited[i] = 1;
                        return graphParam.refrence[i];// 输出有效词
                    }
                }
            }
            for (int j = 0; j < graphParam.getLength(); j++)// 判断是否出现过有效词
            {
                if (ifvisited[j] == 1)
                    return "No more";// 出现过
            }
            return "No have";// 未出现过
        }

    }

    public String generateNewText(Digraph graphParam, String inputText)// 根据bridge word生成新文本
    {
        String[] sentence = inputText.split("\\s+");// 分割输入的字符串
        String strout = new String();// 最终输出的字符串
        String strbridge;// 存放当前桥接词
        String strconnect;// 得出的有效桥接词
        for (int i = 0; i < sentence.length - 1; i++) {
            refreshIfVisited(graphParam.getLength());
            strout = strout + sentence[i] + " ";
            strbridge = graphParam.queryBridgeWords(graphParam, sentence[i], sentence[i + 1]);
            if (strbridge.equals("No in") || strbridge.equals("No have"))
                strconnect = " ";// 如果两词间无桥接词，不需要插入新单词
            else // 如果两词间有桥接词，随机插入一个桥接词
            {
                String[] bridgewords = new String[graphParam.getLength()];
                int index = 0;
                Random random = new Random();
                while (!strbridge.equals("No more"))// 未找全所有桥接词时
                {
                    bridgewords[index] = strbridge;
                    index++;
                    strbridge = graphParam.queryBridgeWords(graphParam, sentence[i], sentence[i + 1]);
                }
                strconnect = bridgewords[random.nextInt(index)] + " ";
            }
            strout = strout + strconnect;
        }
        strout = strout + sentence[sentence.length - 1];
        return strout;

    }

    public String randomWalk(Digraph graphParam)// 随机游走
    {
        int temp;
        final Random index = new Random();// 生成一个范围内的随机数
        temp = index.nextInt(graphParam.getLength());
        if (IfHaveChild(graphParam.refrence[sign]) == false)
            return "end";// 当前节点已无子结点，又走游走结束
        if (graphParam.list[sign][temp] > 0 && ifvisited[temp] == 0)// 当前字符存在邻接字符并且未被访问
        {
            ifvisited[temp] = 1;// 标记访问
            // this.sign=temp;
            // this.refreshtimes();
            sign = temp;
            refreshtimes();
            return graphParam.refrence[temp];// 当前字符为可输出字符，返回当前字符
        } // this.sign
        else if (graphParam.list[sign][temp] > 0 && ifvisited[temp] == 1 && (times == 0 || times == 1))// 当前已经访问过但未重复，说明只出现重复单词，未必是出现重复句子，继续游走
        {
            ifvisited[temp] = 1;// 标记访问
            sign = temp;
            times++;
            return refrence[temp];// 当前字符为可输出字符，返回当前字符
        } else if (graphParam.list[sign][temp] > 0 && ifvisited[temp] == 1 && times == 2)// 当前已经访问过并且已重复，说明出现重复句子，游走结束
        {
            return "end";
        } else// 当前生成随机数不是下一单词的下标但当前走到的节点还有子节点，重新执行函数，直到生成有效下标
        {
            return "continue";
        }
    }

    public void showDirectedGraph(Digraph graphParam)// 展示有向图
    {
        GraphViz gv = new GraphViz();
        gv.addln(gv.start_graph());
        int index = graphParam.getLength();
        for (int i = 0; i < index; i++) {
            for (int j = 0; j < index; j++) {
                if (graphParam.list[i][j] > 0) {
                    gv.addln(graphParam.refrence[i] + "->" + graphParam.refrence[j] + "[label=\"" + graphParam.list[i][j] + "\"];");
                    // gv.addln(graphParam.refrence[i]+"->"+graphParam.refrence[j]+"[label=\""+graphParam.list[i][j]+"\",style=\"dashed\"];");
                }
            }
        }

        gv.addln(gv.end_graph());
        System.out.println(gv.getDotSource());

        String type = "gif";
        File out = new File("/Users/xmh_mac/Documents/JAVA/Lab4/Lab1-1/output." + type);
        gv.writeGraphToFile(gv.getGraph(gv.getDotSource(), type), out);
    }

    // 展示最短路径
    public void showShortestRoute(Digraph graphParam, String route)// 展示有向图
    {
        String[] routes = route.split("->");
        int flag;
        GraphViz gv = new GraphViz();
        gv.addln(gv.start_graph());
        int index = graphParam.getLength();
        for (int i = 0; i < index; i++) {
            for (int j = 0; j < index; j++) {
                if (graphParam.list[i][j] > 0) {
                    // System.out.println(graphParam.list[i][j]);

                    flag = 0;
                    for (int k = 0; k < routes.length - 1; k++) {
                        if (graphParam.refrence[i].equals(routes[k]) && graphParam.refrence[j].equals(routes[k + 1])) {
                            gv.addln(graphParam.refrence[i] + "->" + graphParam.refrence[j] + "[label=\"" + graphParam.list[i][j]
                                    + "\",style=\"dashed\"];");
                            flag = 1;
                            break;
                        }
                    }
                    if (flag == 0) {
                        gv.addln(graphParam.refrence[i] + "->" + graphParam.refrence[j] + "[label=\"" + graphParam.list[i][j] + "\"];");
                    }

                }
            }
        }

        gv.addln(gv.end_graph());
        // System.out.println(gv.getDotSource());

        String type = "gif";
        File out = new File("D:/graphviz2.38/workspace/out2." + type);
        //gv.writeGraphToFile(gv.getGraph(gv.getDotSource(), type), out);
    }

    public String calcShortestPath(Digraph graphParam, String word1, String word2)// 计算两个单词之间的最短路径
    {

        int index1 = graphParam.GetNum(word1);
        int index2 = graphParam.GetNum(word2);
        if (index1 == -1 || index2 == -1)
            return "No way";
        int num = graphParam.getLength();
        int[][] a = new int[num][num];
        int[][] path = new int[num][num];
        int i, j, k, n = num;
        for (i = 0; i < n; i++) {
            for (j = 0; j < n; j++) {
                if (i == j)
                    a[i][j] = 0;
                else if (graphParam.list[i][j] == 0) {
                    a[i][j] = 999;// 无路径时权值定义为999
                }
                else
                    a[i][j] = graphParam.list[i][j];
                path[i][j] = -1;
            }
        }
        for (k = 0; k < n; k++) {
            for (i = 0; i < n; i++) {
                for (j = 0; j < n; j++) {
                    if (a[i][j] > (a[i][k] + a[k][j])) {
                        a[i][j] = a[i][k] + a[k][j];
                        path[i][j] = k;
                    }
                }
            }
        }
        least = a[index1][index2];// 更新最短路径值
        String str;
        str = graphParam.ReturnBetweenWords(path, index1, index2);
        return str;

    }

    public String ReturnBetweenWords(int[][] a, int index1, int index2)// 递归返回字符串，辅助函数
    {
        if (a[index1][index2] == -1)
            return this.refrence[index1] + "->";
        else
            return this.ReturnBetweenWords(a, index1, a[index1][index2])
                    + this.ReturnBetweenWords(a, a[index1][index2], index2);
    }
    
    public void PackageRandomWanderByXmhWnz(Digraph graphParam) {
        String result = new String();
        while (!"end".equals(result))// 未出现结束标志时
        {
            int temp;
            final Random index = new Random();// 生成一个范围内的随机数
            temp = index.nextInt(graphParam.getLength());
            if (IfHaveChild(graphParam.refrence[sign]) == false)
                result = "end";// 当前节点已无子结点，又走游走结束
            if (graphParam.list[sign][temp] > 0 && ifvisited[temp] == 0)// 当前字符存在邻接字符并且未被访问
            {
                ifvisited[temp] = 1;// 标记访问
                // this.sign=temp;
                // this.refreshtimes();
                sign = temp;
                refreshtimes();
                result = graphParam.refrence[temp];// 当前字符为可输出字符，返回当前字符
            } // this.sign
            else if (graphParam.list[sign][temp] > 0 && ifvisited[temp] == 1 && (times == 0 || times == 1))// 当前已经访问过但未重复，说明只出现重复单词，未必是出现重复句子，继续游走
            {
                ifvisited[temp] = 1;// 标记访问
                sign = temp;
                times++;
                result = refrence[temp];// 当前字符为可输出字符，返回当前字符
            } else if (graphParam.list[sign][temp] > 0 && ifvisited[temp] == 1 && times == 2)// 当前已经访问过并且已重复，说明出现重复句子，游走结束
            {
                result = "end";
            } else// 当前生成随机数不是下一单词的下标但当前走到的节点还有子节点，重新执行函数，直到生成有效下标
            {
                result = "continue";
            }
            if (!result.equals("continue") && !result.equals("end")) {
                System.out.print(result + " ");// 当得到有效输出时，输出字符}
            }
        }
        
    }
}

public class Lab1 {

    public static void main(String[] args) {
        // TODO Auto-generated method stub
        Scanner input = new Scanner(System.in);
        String filename = new String("/Users/xmh_mac/Documents/JAVA/Lab4/Lab1-1/myfile.txt");
        String str1, str2, str3;
        final int sizeOfVector = 100;
        Digraph nl = new Digraph(sizeOfVector);// 定义新的有向图类,同时初始化二维矩阵
        nl = nl.createDirectedGraph(filename);// 创建有向图
        nl.printdigraph(nl);
        //nl.showDirectedGraph(nl);
        /* 查询桥接词 */
        System.out.println("Please input the words you want to search: ");
        str1 = input.next();
        input.nextLine();
        str2 = input.next();
        input.nextLine();
        Digraph.refreshIfVisited(nl.getLength());
        str3 = nl.queryBridgeWords(nl, str1, str2);
        if (str3.equals("No in"))
            System.out.println("No \"" + str1 + "\" or \"" + str2 + "\" in the graph !");
        else if (str3.equals("No have"))
            System.out.println("No bridge words from \"" + str1 + "\" to \"" + str2 + "\" !");
        else {
            System.out.print("The bridge words from \"" + str1 + "\" to \"" + str2 + "\" are :" + str3);
            str3 = nl.queryBridgeWords(nl, str1, str2);
            while (!str3.equals("No more")) {
                String str4 = new String(nl.queryBridgeWords(nl, str1, str2));
                if (!str3.equals("No more") && !str4.equals("No more")) {
                    System.out.print("," + str3);
                    str3 = str4;
                } else if (!str3.equals("No more") && str4.equals("No more")) {
                    System.out.print(" and " + str3);
                    str3 = str4;
                }
            }
        }
        System.out.println("\n**********************************************");
        /* 随机游走 */
        System.out.println("Now begin the random waklind.");
        Random random = new Random();
        // String strin=new String("Yes");//输入字符，判断是否继续遍历
        Digraph.sign = random.nextInt(nl.getLength());// 生成一个初始游走位置
        Digraph.refreshIfVisited(nl.getLength());
        Digraph.refreshtimes();
        Digraph.ifvisited[Digraph.sign] = 1;// 标记为已访问
        System.out.print(nl.refrence[Digraph.sign] + " ");
        nl.PackageRandomWanderByXmhWnz(nl);
        /*
         * while(!result.equals("end")&&!strin.equals("No"))//未出现结束标志时 {
         * result=nl.randomWalk(nl); if(!strin.equals("Yes")&&!strin.equals("No")) {
         * System.out.println("Wrong inputs!"); break; }
         * if(!result.equals("continue")&&!result.equals("end")&&strin.equals("Yes")) {
         * System.out.print(result+" \n");//当得到有效输出时，输出字符} System.out.
         * println("Would you like to continue ?(inout Yes to continue, No to stop)");
         * strin=input.next();} }
         */
        System.out.println("\nComplete random walk!");
        System.out.println("**********************************************");
        /* 根据bridgewords生成新文本 */
        System.out.println("Please input the new text: ");
        String inputText = new String(input.nextLine());
        System.out.println(nl.generateNewText(nl, inputText));
        System.out.println("**********************************************");
        /* 最短路径搜索 */
        System.out.println("Please input the words you want to search for the least cost: ");
        String str5, str6, str7;
        str5 = input.next();
        str6 = input.next();
        Digraph.refreshleast();
        String strcheapestroute;
        strcheapestroute = nl.calcShortestPath(nl, str5, str6);
        if (strcheapestroute.equals("No way"))// 如果路径不存在
        {
            System.out.println("No route bewteen the words!");
        } else {
            System.out.println("The least cost way is :" + strcheapestroute + str6);
            nl.showShortestRoute(nl, strcheapestroute + str6);
            System.out.println("The least cost is: " + Digraph.least);
        }
        System.out.println("**********************************************");

        // 单源最短路径
        System.out.println("Please input a word you want to search for the least cost: ");
        str7 = input.next();
        int index = nl.GetNum(str7);
        for (int i = 0; i < nl.getLength(); i++) {
            if (i != index) {
                Digraph.refreshleast();
                String strcheapestroute1 = new String();
                strcheapestroute1 = nl.calcShortestPath(nl, str7, nl.refrence[i]);
                if (strcheapestroute1.equals("No way"))// 如果路径不存在
                {
                    System.out.println("No route bewteen the words!");
                } else {
                    System.out.println("The least cost way is :" + strcheapestroute1 + nl.refrence[i]);
                    // nl.showShortestRoute(nl,strcheapestroute1+str6);
                    System.out.println("The least cost is: " + Digraph.least);
                }
                System.out.println("**********************************************");
            }
        }
        input.close();
        // System.out.println("\nHello World!");

    }

}
