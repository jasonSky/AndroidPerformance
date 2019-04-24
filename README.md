# AndroidPerformance
启动时间（冷/热）+ crash

1. 运行
                  java -jar xxx.jar uuid times 
                  //collectTime 
                  //uuid-device唯一标识  times-运行次数

                  java -jar xxx.jar uuid times alias sleeptime 
                  // crashCollect
                  //uuid-device唯一标识  times-运行次数
                  //alias-logcat保存文件夹名称 sleeptime-每次启动后等待ui显示时间
