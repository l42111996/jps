package org.ksdev.jps.test;

import java.util.ArrayList;

/**
 * 小数点 z,y,z坐标系
 * Created by JinMiao
 * 2021/5/11.
 */
public class FractionalHex {
    public FractionalHex(double q, double r, double s) {
        this.q = q;
        this.r = r;
        this.s = s;
        if (Math.round(q + r + s) != 0) throw new IllegalArgumentException("q + r + s must be 0");
    }

    public final double q;
    public final double r;
    public final double s;

    /**
     * 四舍五入转换成z,y,z坐标系
     * @return
     */
    public Hex hexRound() {
        int qi = (int) (Math.round(q));
        int ri = (int) (Math.round(r));
        int si = (int) (Math.round(s));
        double q_diff = Math.abs(qi - q);
        double r_diff = Math.abs(ri - r);
        double s_diff = Math.abs(si - s);
        if (q_diff > r_diff && q_diff > s_diff) {
            qi = -ri - si;
        } else if (r_diff > s_diff) {
            ri = -qi - si;
        } else {
            si = -qi - ri;
        }
        return new Hex(qi, si, ri);
    }


    public FractionalHex hexLerp(FractionalHex b, double t) {
        return new FractionalHex(q * (1.0 - t) + b.q * t, r * (1.0 - t) + b.r * t, s * (1.0 - t) + b.s * t);
    }


    static public ArrayList<Hex> hexLinedraw(Hex a, Hex b) {
        int N = a.distance(b);
        FractionalHex a_nudge = new FractionalHex(a.x + 1e-06, a.z + 1e-06, a.y - 2e-06);
        FractionalHex b_nudge = new FractionalHex(b.x + 1e-06, b.z + 1e-06, b.y - 2e-06);
        ArrayList<Hex> results = new ArrayList<Hex>() {{
        }};
        double step = 1.0 / Math.max(N, 1);
        for (int i = 0; i <= N; i++) {
            results.add(a_nudge.hexLerp(b_nudge, step * i).hexRound());
        }
        return results;
    }

}
