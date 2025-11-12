package org.example.models;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

public class Params {
    private final int x;
    private final double y;
    private final double r;

    public Params(Map<String, String[]> paramMap) {
        Map<String, String> params = flatten(paramMap);
        this.x = Integer.parseInt(params.get("x"));
        this.y = new BigDecimal(params.get("y")).doubleValue();
        this.r = new BigDecimal(params.get("r")).doubleValue();
    }

    private static Map<String, String> flatten(Map<String, String[]> in) {
        Map<String, String> out = new HashMap<>();
        if (in == null) return out;
        in.forEach((k, v) -> out.put(k, (v == null || v.length == 0) ? null : v[0]));
        return out;
    }


    public int getX() { return x; }
    public double getY() { return y; }
    public double getR() { return r; }
}
