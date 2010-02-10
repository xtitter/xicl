package ru.icl.dicewars.sample;

public class Calc {
	private static long[][] a = new long[9][49];

	private static double calc(int n, int k) {
		if (n <= 0 || n > 8 || k <= 0 || k > 8)
			throw new IllegalArgumentException();
		long r1 = 0;
		for (int i = 0; i < 49; i++) {
			long r = 0;
			for (int j = 0; j < i; j++) {
				r = r + a[k][j];
			}
			r1 = r1 + r * a[n][i];
		}

		long s1 = 0;
		long s2 = 0;
		for (int i = 0; i < 49; i++) {
			s1 = s1 + a[n][i];
			s2 = s2 + a[k][i];
		}

		double d1 = r1;
		double d2 = s1 * s2;
		return d1 / d2;
	}

	private static void initCalc() {
		for (int i = 0; i < 9; i++)
			for (int j = 0; j < 49; j++)
				a[i][j] = 0;

		for (int i1 = 1; i1 < 7; i1++) {
			a[1][i1]++;
			for (int i2 = 1; i2 < 7; i2++) {
				a[2][i1 + i2]++;
				for (int i3 = 1; i3 < 7; i3++) {
					a[3][i1 + i2 + i3]++;
					for (int i4 = 1; i4 < 7; i4++) {
						a[4][i1 + i2 + i3 + i4]++;
						for (int i5 = 1; i5 < 7; i5++) {
							a[5][i1 + i2 + i3 + i4 + i5]++;
							for (int i6 = 1; i6 < 7; i6++) {
								a[6][i1 + i2 + i3 + i4 + i5 + i6]++;
								for (int i7 = 1; i7 < 7; i7++) {
									a[7][i1 + i2 + i3 + i4 + i5 + i6 + i7]++;
									for (int i8 = 1; i8 < 7; i8++) {
										a[8][i1 + i2 + i3 + i4 + i5 + i6 + i7
												+ i8]++;
									}
								}
							}
						}
					}
				}
			}
		}
	}

	public static void main(String[] args) {
		initCalc();
		System.out.println(calc(8,8)*calc(7,4));
	}
}
