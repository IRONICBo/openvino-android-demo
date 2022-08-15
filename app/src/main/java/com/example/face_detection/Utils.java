package com.example.face_detection;

public class Utils {
    public static float[] sigmoidArrayOut(float[] x) {
        float[] out = new float[x.length];
        for (int i = 0; i < x.length; i++) {
            out[i] = sigmoidF(x[i]);
        }
        return out;
    }

    public static float sigmoidF(double x) {
        return (float) (1f / (1f + Math.pow(Math.E, -1 * x)));
    }

    public static float[] expArrayOut(float[] x) {
        float[] out = new float[x.length];
        for (int i = 0; i < x.length; i++) {
            out[i] = exp(x[i]);
        }
        return out;
    }

    public static float exp(float x) {
        return (float) Math.pow(Math.E, x);
    }

    public static void multiplyArr(float[] arr, float t) {
        for (int i = 0; i < arr.length; i++) {
            arr[i] *= t;
        }
    }

    public static int findMaxIdx(float[] arr) {
        int idx = 0;
        float tmp = arr[0];
        for (int i = 1; i < arr.length; i++) {
            if (tmp < arr[i]) {
                tmp = arr[i];
                idx = i;
            }
        }
        return idx;
    }
}
