package com.example.face_detection;

import org.opencv.core.Rect2d;

import java.util.ArrayList;
import java.util.List;

public class YOLOProcess {
    public static void YOLODecode(float[] YOLOOutputs, int[][] anchors, int featureSize,
                                  List<Rect2d> rect2dList, List<Float> confList, List<Float> clsList,
                                  List<Integer> objIndexList, double thresh){
        int boxesNum = YOLOOutputs.length / 85; // 计数的次数

        // 先x方向增加，再y方向增加，dx & dy 相当于13/26进制
        int dx = 0;
        int dy = 0;
        int da = 0; // anchor的变化

        for (int i = 0; i < boxesNum; i++) {
            float x = YOLOOutputs[i * 85 + 0];
            float y = YOLOOutputs[i * 85 + 1];
            float h = YOLOOutputs[i * 85 + 2]; // todo: 这里替换了 h -> w
            float w = YOLOOutputs[i * 85 + 3];
            float boxScore = YOLOOutputs[i * 85 + 4];

            boxScore = Utils.sigmoidF(boxScore);

            if (boxScore <= thresh) {
                // 后置处理
                da ++;
                if (da >= 3) {
                    da = 0;

                    // 等一个anchor计算完成后，再移动dx, dy，一个cell上需要计算三次
                    dx ++;
                    if (dx >= featureSize) {
                        dx = 0;
                        dy ++;
                    }
                    if (dy >= featureSize) {
                        dx = 0;
                        dy = 0;
                    }
                }
                continue;
            }

            // 整个图的绝对位置
            x = (Utils.sigmoidF(x) + dx) / featureSize * 416; // ratio -> 先统一量纲
            y = (Utils.sigmoidF(y) + dy) / featureSize * 416;

            h = (Utils.exp(h) * anchors[da][0]); // 0为w, 1为h
            w = (Utils.exp(w) * anchors[da][1]); // 0为w, 1为h

            // convert centoids to top left coordinates
            x = x - (w / 2);
            y = y - (h / 2);


            for (int j = i * 85 + 5; j < i * 85 + 85; j ++) {
                YOLOOutputs[j] = Utils.sigmoidF(YOLOOutputs[j]);
            }

            int maxIdx = i * 85 + 5;
            float maxConf = YOLOOutputs[i * 85 + 5];
            for (int j = i * 85 + 6; j < i * 85 + 85; j ++) {
                if (YOLOOutputs[j] > maxConf) {
                    maxConf = YOLOOutputs[j];
                }
            }

            // todo : 切换notebook里面的模型版本，测试一下是否能够正常使用。。。
            // 推理还是有点问题的，比如这个idx的问题
            // 但是输出结果也有问题，需要改一下。。。

            // 添加到结果列表
            rect2dList.add(new Rect2d(x, y, w, h));
            confList.add(boxScore);
            clsList.add(maxConf);
            objIndexList.add(maxIdx - (i * 85 + 5));

            // 由于前面的限制，有的跳过了
            System.out.println("num : === " + i + " base idx : " + i * 85 + " dx : "  + dx + " dy : " + dy + " da : " + da);

            // 后置处理
            da ++;
            if (da >= 3) {
                da = 0;

                // 等一个anchor计算完成后，再移动dx, dy，一个cell上需要计算三次
                dx ++;
                if (dx >= featureSize) {
                    dx = 0;
                    dy ++;
                }
                if (dy >= featureSize) {
                    dx = 0;
                    dy = 0;
                }
            }
//            dx ++;
//            if (dx >= featureSize) {
//                dx = 0;
//                dy ++;
//            }
//            if (dy >= featureSize) {
//                dx = 0;
//                dy = 0;
//            }
        }
    }
}
