/* WARANTY NOTICE AND COPYRIGHT
This program is free software; you can redistribute it and/or
modify it under the terms of the GNU General Public License
as published by the Free Software Foundation; either version 2
of the License, or (at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program; if not, write to the Free Software
Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.

Copyright (C) Michael J. Meyer

matmjm@mindspring.com
spyqqqdia@yahoo.com

 */


/*
 * Sobol.java
 *
 * Created on April 18, 2002, 12:19 PM
 */

package tuxkids.tuxblocks.core.utils;

/** <p>Generator for the Sobol sequence. Uses the Gray code counter and bitwise 
  operations for very fast point generation.</p>

  <p>Use N=2^n-1, n=1,2,... points for QMC integration.
  At N=2^n-1 the Gray code counter G(k) is in sync with the integer sequence
  k=1,2,... again, that is { G(1),...,G(N) }={ 1,...,N }.</p>

  <p><b>Dimension:</b> the Sobol generator is implemented only in dimension
  at most 300. The current implementation relies on primitive polynomials 
  and initialization numbers from the book [J]:
  <i>Monte Carlo Methods in Finance</i> by Peter Jaeckel, Wiley,
  ISNB 047149741X. The CD sold with the book contains millions of 
  primitive polynomials allowing you to extend the generator to millions of 
  dimensions.</p>

  <p>If the dimension is small low discrepancy sequences are significantly 
  better Monte Carlo integrators than uniform sequences while this advantage 
  seems to fade as the dimension increases at least if the number N of points
  is restricted to values that are realistic in applications.</p>

  <p>This would argue that we apply the Sobol sequence to a small number of
  important dimensions while driving the remaining dimensions with a uniform
  sequence. On the other hand [J] presents evidence that the Sobol sequence
  keeps up with the uniform sequence at any number N of points even in high 
  dimensions if the initialization numbers are chosen properly.</p>

  <p>In this regard it should be noted that even the best uniform random number 
  generator, the Mersenne Twister is only known to deliver an equidistributed
  sequence up to dimension 623. If the sequence is not equidistributed
  we do not know wether the Monte Carlo integral converges to the true integral
  as the number N of points inreases to infinity. Low discrepancy sequences on 
  the other hand are equidistributed in every dimension and so the Monte
  Carlo integral is guarenteed to converge to the true value of the integral.
  </p>

  <p>The reader is advised to consult [J] for a detailed description of 
  techniques to reduce effective dimensionality and much additional 
  source code related to Monte Carlo simulation. It is an excellent reference 
  on the topic.</p>
 * 
 *
 * @author  Michael J. Meyer
 */
public class Sobol extends LowDiscrepancySequence {

	static final int bits=32;            // we are using 32 bit integers
	static final long N=4294967296L;     // 2^32


	long[][] v;   // v[k] - array of direction numbers for dimension k

	int[][] p;    // p[k] - coefficient array of the k-th primitive polynomial

	int[] g;      // g[k] - degree of the k-th primitive polynomial

	long[] x_int;    // current vector of Sobol integers



	@Override
	public String getName(){ return "Sobol Sequence"; }    


	/*******************************************************************************

           STATIC METHODS FOR BINARY OPERATIONS

	 *******************************************************************************/   

	/** Print binary string representation of a positive integer n.
	 *  Most significant digit leftmost as usual.
	 */
	static void printbin(int n)
	{
		if(n>0)
		{ printbin(n/2); System.out.print(n%2); }
	}

	/** The Gray code of n.
	 */
	public static int gray(int n){ return n^(n/2); }

	/** <p>A primitive polynomial p(x) modulo 2 is encoded by a pair of numbers 
	 *  (d,n) as follows: d=degree(p), the leading and trailing coefficient of p
	 *  are 1 and the intermediate coefficients are the bits of n in the binary
	 *  representation of n: for example the polynomial</p>
	 *
	 * <center> 1+x+x^2+x^4+x^5 </center>
	 *
	 * <p> with coefficients (1)1101(1) is encoded as (5,n) with n=1101=13.
	 *  In other words the least significant bit of n corresponds to the 
	 *  second highest power of x etc.</p>
	 *
	 *  <p> The routine allocates the coefficient array prim_pol[k] of this 
	 *  polynomial and writes the coefficients into the array with powers of x 
	 *  decreasing left to right.</p>
	 *
	 * @param d,n encodings of polynomial
	 * @param k polynomial to be stored as prim_pol[k]
	 */
	void read_prim_pol(int d, int n, int k)
	{
		p[k]=new int[d+1];

		int j=0; p[k][d]=1;
		while(n>0){ j++; p[k][d-j]=n%2; n=n/2; }
		p[k][0]=1;

	}




	/*******************************************************************************

                                  CONSTRUCTOR

	 *******************************************************************************/   


	/**
	 * @param dim dimension of the Sobol sequence.
	 */
	public Sobol(int dim)
	{
		super(dim);
		if(dim>300){ System.out.println
			("Sobol sequence only implemented for dimension at most 300."+
					"\nExiting.");
		//System.exit(0);   }
		}


		// array of polynomial degrees and coefficient arrays
		g=new int[dim];
		p=new int[dim][];
		// degree zero
		g[0]=0;
		p[0]=new int[1]; p[0][0]=1;
		// positive degree polynomials read from pp
		int k=1;
		for(int i=0;i<pp.length;i++)
			for(int j=0;j<pp[i].length;j++)
				if(k<dim){ g[k]=i+1; read_prim_pol(g[k],pp[i][j],k); k++; }
				else break;

		/* DEBUG: print the primitive polynomials:
System.out.println("\nPrimitive polynomials:\n");
for(k=0;k<50;k++)
{  
    for(int j=0;j<=g[k];j++)System.out.print(p[k][j]);
    System.out.println(""); 
}
		 */                                 




		// initialize the array of direction integers
		v=new long[dim][bits];
		for(int j=0;j<bits;j++)v[0][j]=(1L<<(bits-j-1));

		if(dim>1)
			v[1][0]=(1L<<bits-1);

		if(dim>2){
			v[2][0]=(1L<<bits-1); 
			v[2][1]=(1L<<bits-2); }

		if(dim>3){
			v[3][0]=(1L<<bits-1); 
			v[3][1]=(3L<<bits-2); 
			v[3][2]=(7L<<bits-3); }

		if(dim>4){
			v[4][0]=(1L<<bits-1); 
			v[4][1]=(1L<<bits-2); 
			v[4][2]=(5L<<bits-3); }

		if(dim>5){
			v[5][0]=(1L<<bits-1); 
			v[5][1]=(3L<<bits-2); 
			v[5][2]=(1L<<bits-3); 
			v[5][3]=(1L<<bits-4); }

		if(dim>6){
			v[6][0]=(1L<<bits-1); 
			v[6][1]=(1L<<bits-2); 
			v[6][2]=(3L<<bits-3); 
			v[6][3]=(7L<<bits-4); }

		if(dim>7){
			v[7][0]=(1L<<bits-1); 
			v[7][1]=(3L<<bits-2); 
			v[7][2]=(3L<<bits-3); 
			v[7][3]=(9L<<bits-4); 
			v[7][4]=(9L<<bits-5); }

		if(dim>8){
			v[8][0]=(1L<<bits-1); 
			v[8][1]=(3L<<bits-2); 
			v[8][2]=(7L<<bits-3); 
			v[8][3]=(7L<<bits-4); 
			v[8][4]=(21L<<bits-5); }

		if(dim>9){
			v[9][0]=(1L<<bits-1); 
			v[9][1]=(1L<<bits-2); 
			v[9][2]=(5L<<bits-3); 
			v[9][3]=(11L<<bits-4); 
			v[9][4]=(27L<<bits-5); }

		if(dim>10){
			v[10][0]=(1L<<bits-1); 
			v[10][1]=(1L<<bits-2); 
			v[10][2]=(7L<<bits-3); 
			v[10][3]=(3L<<bits-4); 
			v[10][4]=(29L<<bits-5); }

		if(dim>11){
			v[11][0]=(1L<<bits-1); 
			v[11][1]=(3L<<bits-2); 
			v[11][2]=(7L<<bits-3); 
			v[11][3]=(13L<<bits-4);
			v[11][4]=(3L<<bits-5); }

		if(dim>12){
			v[12][0]=(1L<<bits-1); 
			v[12][1]=(3L<<bits-2); 
			v[12][2]=(5L<<bits-3); 
			v[12][3]=(1L<<bits-4); 
			v[12][4]=(15L<<bits-5); }

		if(dim>13){
			v[13][0]=(1L<<bits-1); 
			v[13][1]=(1L<<bits-2); 
			v[13][2]=(1L<<bits-3); 
			v[13][3]=(9L<<bits-4); 
			v[13][4]=(23L<<bits-5);
			v[13][5]=(37L<<bits-6); }

		if(dim>14){
			v[14][0]=(1L<<bits-1); 
			v[14][1]=(1L<<bits-2); 
			v[14][2]=(3L<<bits-3); 
			v[14][3]=(13L<<bits-4); 
			v[14][4]=(11L<<bits-5);
			v[14][5]=(7L<<bits-6); }

		if(dim>15){
			v[15][0]=(1L<<bits-1); 
			v[15][1]=(3L<<bits-2); 
			v[15][2]=(3L<<bits-3); 
			v[15][3]=(5L<<bits-4); 
			v[15][4]=(19L<<bits-5);
			v[15][5]=(33L<<bits-6); }

		if(dim>16){
			v[16][0]=(1L<<bits-1); 
			v[16][1]=(1L<<bits-2); 
			v[16][2]=(7L<<bits-3); 
			v[16][3]=(13L<<bits-4); 
			v[16][4]=(25L<<bits-5);
			v[16][5]=(5L<<bits-6); }

		if(dim>17){
			v[17][0]=(1L<<bits-1); 
			v[17][1]=(1L<<bits-2); 
			v[17][2]=(1L<<bits-3); 
			v[17][3]=(13L<<bits-4); 
			v[17][4]=(15L<<bits-5);
			v[17][5]=(39L<<bits-6); }

		if(dim>18){
			v[18][0]=(1L<<bits-1); 
			v[18][1]=(3L<<bits-2); 
			v[18][2]=(5L<<bits-3); 
			v[18][3]=(11L<<bits-4); 
			v[18][4]=(7L<<bits-5);
			v[18][5]=(11L<<bits-6); }

		if(dim>19){
			v[19][0]=(1L<<bits-1); 
			v[19][1]=(3L<<bits-2); 
			v[19][2]=(1L<<bits-3); 
			v[19][3]=(7L<<bits-4); 
			v[19][4]=(3L<<bits-5);
			v[19][5]=(23L<<bits-6);
			v[19][6]=(79L<<bits-7); }

		if(dim>20){
			v[20][0]=(1L<<bits-1); 
			v[20][1]=(3L<<bits-2); 
			v[20][2]=(1L<<bits-3); 
			v[20][3]=(15L<<bits-4); 
			v[20][4]=(17L<<bits-5);
			v[20][5]=(63L<<bits-6);
			v[20][6]=(13L<<bits-7); }

		if(dim>21){
			v[21][0]=(1L<<bits-1); 
			v[21][1]=(3L<<bits-2); 
			v[21][2]=(3L<<bits-3); 
			v[21][3]=(3L<<bits-4); 
			v[21][4]=(25L<<bits-5);
			v[21][5]=(17L<<bits-6);
			v[21][6]=(115L<<bits-7); }

		if(dim>22){
			v[22][0]=(1L<<bits-1); 
			v[22][1]=(3L<<bits-2); 
			v[22][2]=(7L<<bits-3); 
			v[22][3]=(9L<<bits-4); 
			v[22][4]=(31L<<bits-5);
			v[22][5]=(29L<<bits-6);
			v[22][6]=(17L<<bits-7); }

		if(dim>23){
			v[23][0]=(1L<<bits-1); 
			v[23][1]=(1L<<bits-2); 
			v[23][2]=(3L<<bits-3); 
			v[23][3]=(15L<<bits-4); 
			v[23][4]=(29L<<bits-5);
			v[23][5]=(15L<<bits-6);
			v[23][6]=(41L<<bits-7); }

		if(dim>24){
			v[24][0]=(1L<<bits-1); 
			v[24][1]=(3L<<bits-2); 
			v[24][2]=(1L<<bits-3); 
			v[24][3]=(9L<<bits-4); 
			v[24][4]=(5L<<bits-5);
			v[24][5]=(21L<<bits-6);
			v[24][6]=(119L<<bits-7); }

		if(dim>25){
			v[25][0]=(1L<<bits-1); 
			v[25][1]=(1L<<bits-2); 
			v[25][2]=(5L<<bits-3); 
			v[25][3]=(5L<<bits-4); 
			v[25][4]=(1L<<bits-5);
			v[25][5]=(27L<<bits-6); 
			v[25][6]=(33L<<bits-7); }

		if(dim>26){
			v[26][0]=(1L<<bits-1); 
			v[26][1]=(1L<<bits-2); 
			v[26][2]=(3L<<bits-3); 
			v[26][3]=(1L<<bits-4); 
			v[26][4]=(23L<<bits-5);
			v[26][5]=(13L<<bits-6);
			v[26][6]=(75L<<bits-7); }

		if(dim>27){
			v[27][0]=(1L<<bits-1); 
			v[27][1]=(1L<<bits-2); 
			v[27][2]=(7L<<bits-3); 
			v[27][3]=(7L<<bits-4); 
			v[27][4]=(19L<<bits-5);
			v[27][5]=(25L<<bits-6);
			v[27][6]=(105L<<bits-7); }

		if(dim>28){
			v[28][0]=(1L<<bits-1); 
			v[28][1]=(3L<<bits-2); 
			v[28][2]=(5L<<bits-3); 
			v[28][3]=(5L<<bits-4); 
			v[28][4]=(21L<<bits-5);
			v[28][5]=(9L<<bits-6);
			v[28][6]=(7L<<bits-7); }

		if(dim>29){
			v[29][0]=(1L<<bits-1); 
			v[29][1]=(1L<<bits-2); 
			v[29][2]=(1L<<bits-3); 
			v[29][3]=(15L<<bits-4); 
			v[29][4]=(5L<<bits-5);
			v[29][5]=(49L<<bits-6);
			v[29][6]=(59L<<bits-7); }

		if(dim>30){
			v[30][0]=(1L<<bits-1); 
			v[30][1]=(3L<<bits-2); 
			v[30][2]=(5L<<bits-3); 
			v[30][3]=(15L<<bits-4); 
			v[30][4]=(17L<<bits-5);
			v[30][5]=(19L<<bits-6);
			v[30][6]=(21L<<bits-7); }

		if(dim>31){
			v[31][0]=(1L<<bits-1); 
			v[31][1]=(1L<<bits-2); 
			v[31][2]=(7L<<bits-3); 
			v[31][3]=(11L<<bits-4); 
			v[31][4]=(13L<<bits-5);
			v[31][5]=(29L<<bits-6);
			v[31][6]=(3L<<bits-7); }

		// random initialization in dimension bigger than 32

		for(k=32;k<dim;k++)
		{   
			for(int l=0;l<g[k];l++)
			{
				//double u=Random.U1();
				double u=Math.random();
				long f=(1L<<l+1), n=(int)(f*u);
				//while(n%2==0){ u=Random.U1(); n=(int)(f*u); }
				while(n%2==0){ u=Math.random(); n=(int)(f*u); }

				v[k][l]=(n<<(bits-l-1));
			}
		} // end direction integer initialization




		// computation of direction integer v_kl for k>=degree[k]
		for(k=1;k<dim;k++)
			for(int l=g[k];l<bits;l++)
			{
				long n=(v[k][l-g[k]]>>g[k]);
				for(int j=1;j<=g[k];j++) if(p[k][j]!=0) n=n^v[k][l-j];

				v[k][l]=n;

			}



		// initialize the vector of Sobol integers and Sobol points
		index=1;
		x_int=new long[dim];
		for(k=0;k<dim;k++) x_int[k]=v[k][0];

		x=new double[dim];
		for(k=0;k<dim;k++) x[k]=((double)x_int[k])/N;



	}// end constructor


	/*******************************************************************************

           THE SOBOL POINTS

	 *******************************************************************************/   


	@Override
	public void restart()
	{
		index=1;
		// return the integer vector to the initial state
		for(int k=0;k<dim;k++)x_int[k]=v[k][0];
	}


	/** The next Sobol point in the unit cube [0,1]^dim.
	 */
	@Override
	public double[] nextPoint()
	{
		// find the rightmost zero bit of index
		int j=0, n=index;
		while(n%2==1){ n=n>>1; j++; }

		for(int k=0;k<dim;k++) { x_int[k]^=v[k][j];
		x[k]=((double)x_int[k])/N; }

		index++;
		return x;
	}




	/*******************************************************************************

           TEST PROGRAM

	 *******************************************************************************/   

	/**  Small test program, allocates a Sobol generator and prints several
	 *   Sobol points.
	 */
//	public static void main(String[] args)
//	{
//		Sobol300 S=new Sobol300(20);
//
//
//		for(int k=0;k<12;k++)
//		{  
//			int d=k+1;
//			System.out.println("\n\nk="+d+"\n");
//			for(int j=0;j<S.g[k];j++)
//				System.out.println(S.v[k][j]);
//			System.out.println("\n"+S.v[k][9]);
//		}
//
//
//		for(int j=0;j<300;j++)
//		{
//			System.out.println("Sobol point "+j+":");
//			double[] x=S.nextPoint();
//			System.out.print(x[7]+", ");
//		}
//
//	}

	/*******************************************************************************

          ENCODED PRIMITIVE POLYNOMIALS MODULO TWO

	 *******************************************************************************/

	/** The list pp of primitive polynomials, pp[j] is the array of encodings n of
	 *  primitive polynomials of degree j+1. The polynomial p(x)=1 of degree zero
	 *  is dealt with separately
	 */
	public static final int[][] pp={ 
		//degree 1    
		{ 0 }, 
		// degree 2
		{ 1 }, 
		// degree 3
		{ 1,2 }, 
		// degree 4
		{ 1,4 },
		//degree 5
		{ 2,
			4,
			7,
			11,
			13,
			14 },
			//degree 6
			{ 1,
				13,
				16,
				19,
				22,
				25 },
				// degree 7
				{ 1,
					4,
					7,
					8,
					14,
					19,
					21,
					28,
					31,
					32,
					37,
					41,
					42,
					50,
					55,
					56,
					59,
					62 },
					// degree 8
					{ 14,
						21,
						22,
						38,
						47,
						49,
						50,
						52,
						56,
						67,
						70,
						84,
						97,
						103,
						115,
						122 },
						// degree 9
						{ 8,
							13,
							16,
							22,
							25,
							44,
							47,
							52,
							55,
							59,
							62,
							67,
							74,
							81,
							82,
							87,
							91,
							94,
							103,
							104,
							109,
							122,
							124,
							137,
							138,
							143,
							145,
							152,
							157,
							167,
							173,
							176,
							181,
							182,
							185,
							191,
							194,
							199,
							218,
							220,
							227,
							229,
							230,
							234,
							236,
							241,
							244,
							253 },
							//degree 10
							{ 4,
								13,
								19,
								22,
								50,
								55,
								64,
								69,
								98,
								107,
								115,
								121,
								127,
								134,
								140,
								145,
								152,
								158,
								161,
								171,
								181,
								194,
								199,
								203,
								208,
								227,
								242,
								251,
								253,
								265,
								266,
								274,
								283,
								289,
								295,
								301,
								316,
								319,
								324,
								346,
								352,
								361,
								367,
								382,
								395,
								398,
								400,
								412,
								419,
								422,
								426,
								428,
								433,
								446,
								454,
								457,
								472,
								493,
								505,
								508 },
								//degree 11
								{ 2,
									11,
									21,
									22,
									35,
									49,
									50,
									56,
									61,
									70,
									74,
									79,
									84,
									88,
									103,
									104,
									112,
									115,
									117,
									122,
									134,
									137,
									146,
									148,
									157,
									158,
									162,
									164,
									168,
									173,
									185,
									186,
									191,
									193,
									199,
									213,
									214,
									220,
									227,
									236,
									242,
									251,
									256,
									259,
									265,
									266,
									276,
									292,
									304,
									310,
									316,
									319,
									322,
									328,
									334,
									339,
									341,
									345,
									346,
									362,
									367,
									372,
									375,
									376,
									381,
									385,
									388,
									392,
									409,
									415,
									416,
									421,
									428,
									431,
									434,
									439,
									446,
									451,
									453,
									457,
									458,
									471,
									475,
									478,
									484,
									493,
									494,
									499,
									502,
									517,
									518,
									524,
									527,
									555,
									560,
									565,
									569,
									578,
									580,
									587,
									589,
									590,
									601,
									607,
									611,
									614,
									617,
									618,
									625,
									628,
									635,
									641,
									647,
									654,
									659,
									662,
									672,
									675,
									682,
									684,
									689,
									695,
									696,
									713,
									719,
									724,
									733,
									734,
									740,
									747,
									749,
									752,
									755,
									762,
									770,
									782,
									784,
									787,
									789,
									793,
									796,
									803,
									805,
									810,
									815,
									824,
									829,
									830,
									832,
									841,
									847,
									849,
									861,
									871,
									878,
									889,
									892,
									901,
									908,
									920,
									923,
									942,
									949,
									950,
									954,
									961,
									968,
									971,
									973,
									979,
									982,
									986,
									998,
									1001,
									1010,
									1012 },
									// degree 12
									{ 41,
										52,
										61,
										62,
										76,
										104,
										117,
										131,
										143,
										145,
										157,
										167,
										171,
										176,
										181,
										194,
										217,
										236 }

	}; // end pp




} // end Sobol

