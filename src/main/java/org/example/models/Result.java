package org.example.models;

public class Result {
    private final String time;
    private final int x;
    private final double y;
    private final double r;
    private final boolean hit;

    public Result(String time, int x, double y, double r, boolean hit) {
        this.time = time;
        this.x = x;
        this.y = y;
        this.r = r;
        this.hit = hit;
    }

    public String getTime() { return time; }
    public int getX() { return x; }
    public double getY() { return y; }
    public double getR() { return r; }
    public boolean isHit() { return hit; }
}


