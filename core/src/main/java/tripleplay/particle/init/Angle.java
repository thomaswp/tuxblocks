package tripleplay.particle.init;

import static tripleplay.particle.ParticleBuffer.M00;
import static tripleplay.particle.ParticleBuffer.M01;
import static tripleplay.particle.ParticleBuffer.M10;
import static tripleplay.particle.ParticleBuffer.M11;
import static tripleplay.particle.ParticleBuffer.VEL_X;
import static tripleplay.particle.ParticleBuffer.VEL_Y;
import pythagoras.f.FloatMath;
import tripleplay.particle.Initializer;

public class Angle {
	
	public static Initializer byVelocity() {
		return byVelocity(0);
	}
	
	public static Initializer byVelocity(final float angleOffset) {
		return new Initializer() {
			@Override
			public void init(int index, float[] data, int start) {
				double angle = Math.atan2(data[start + VEL_Y], data[start + VEL_X]) + angleOffset;
				set(data, start, (float)angle);
				
			}
		};
	}
	
	private static void set(float[] data, int start, float angle) {
		float m00 = data[start + M00];
		float m01 = data[start + M01];
		float m10 = data[start + M10];
		float m11 = data[start + M11];
		float cosT = FloatMath.cos(angle);
		float sinT = FloatMath.sin(angle);
		data[start + M00] = m00 * cosT - m10 * sinT;
		data[start + M01] = m00 * sinT + m10 * cosT;
		data[start + M10] = m01 * cosT - m11 * sinT;
		data[start + M11] = m01 * sinT + m11 * cosT;
	}
}
