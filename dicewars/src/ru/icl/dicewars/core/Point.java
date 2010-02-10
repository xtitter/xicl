package ru.icl.dicewars.core;

import java.io.Serializable;

public class Point implements Serializable{
	private Integer x;
	private Integer y;
	
	private volatile int hashCode;

	public Point(Integer x, Integer y) {
		//if (x == null || y == null || x < 0 || y < 0)
			//throw new IllegalArgumentException();
		this.x = x;
		this.y = y;
	}

	public Integer getX() {
		return x;
	}

	public Integer getY() {
		return y;
	}

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof Point))
			return false;
		Point p = (Point) obj;
		return this.x.equals(p.x) && this.y.equals(p.y);
	}
	
	@Override
    public int hashCode() {
         int result = hashCode;
         if (result == 0) {
              result = 17;
              result = 31 * result + x;
              result = 31 * result + y;
              hashCode = result;
         }
         return result;
    }
}
