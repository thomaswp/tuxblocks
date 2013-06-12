/*
 * LowDiscrepancySequence.java
 *
 * Created on April 26, 2002, 6:04 PM
 */

package tuxkids.tuxblocks.core.utils;


/** <p>Interface and methods to compute L2-discrepancy for low
 *  discrepancy sequences.</p>
 *
 * @author  Michael J. Meyer
 */
public abstract class LowDiscrepancySequence {
    
    int dim;           // dimension
    int index;         // index of current point in sequence
    
    double[] x;         // current uniform point
    double[] z;         // current quasi normal transform of x
    
    /**
     * @param dim dimension
     */
    public LowDiscrepancySequence(int dim) 
    { 
        this.dim=dim; 
        index=1; 
        x=new double[dim];
        z=new double[dim];
    }
    
    /** Name of sequence.
     */
    public abstract String getName();
    

/*******************************************************************************
 
           POINT GENERATION
 
*******************************************************************************/
    

    public void restart(){ index=1; }

    /** Returns the next point in the sequence.
     */
    public abstract double[] nextPoint();
    
    /** <p>Writes the next point of the sequence into 
     *  the array r. Useful when discrepancy is computed.</p>
     */
    public void nextPoint(double r[])
    {
        double[] x=nextPoint();
        for(int k=0;k<dim;k++) r[k]=x[k];
    }
     
    
    
/*******************************************************************************
 
           L^2-DISCREPANCY
 
*******************************************************************************/   
    
     
     // r[j][] is the jth point in the low discrepancy sequence
     double product(int i, int j, double r[][])
     {
         double f=1;
         for(int k=0;k<dim;k++)f*=(1-Math.max(r[i][k],r[j][k]));
         
         return f;
     }
         

     // r[j][] is the jth point in the low discrepancy sequence
     double product(int i, double r[][])
     {
         double f=1;
         for(int k=0;k<dim;k++)f*=(1-r[i][k]);
         
         return f;
     }
     
     
     // r[j][] is the jth point in the low discrepancy sequence
     double productSQ(int i, double r[][])
     {
         double f=1;
         for(int k=0;k<dim;k++)f*=(1-r[i][k]*r[i][k]);
         
         return f;
     }  
    
    
    /** <p>The L^2-discrepancy of the first N points.
     *  This computes the L^2 discrepancy T_N of the first N points 
     *  r[j][], j=0,...,N-1.</p>
     *
     * @param N number of points.
     * @param r array of points r[j][], j=0,...,N-1.
     */    
//     public double L2_discrepancy(int N, double[][] r)
//     {        
//        // compute a=1/2^d, b=1/3^d
//        double a=1, b=1;
//        for(int k=0;k<dim;k++){ a/=2; b/=3; }
//        
//        double sum_1=0, sum_2=0;
//        
//        // report progress on main loop
//        LoopStatus loopStatus=new LoopStatus();
//        long before=System.currentTimeMillis();
//        int loops=0;
//        
//        // first sum over 1<=i<j<=N
//        for(int i=0;i<N;i++)
//        for(int j=i+1;j<N;j++)
//        {
//            sum_1+=product(i,j,r);
//
//            // progress report, algorithm is of order N^2
//            loops++;
//            if(loops%100000==99999)
//            loopStatus.progressReport(loops,N*(N-1)/2,100000,before); 
//        }
//
//        // first sum over 1<=i\neq j<=N
//        sum_1*=2;
//        
//        // add in the terms for i=j
//        for(int i=0;i<N;i++)sum_1+=product(i,r);
//        
//        sum_1/=(N*N);
//        
//        // second sum
//        for(int i=0;i<N;i++)sum_2+=productSQ(i,r);
//        sum_2/=N; sum_2*=(2*a);
//        
//        return Math.sqrt(sum_1-sum_2+b);
//     
//     } // end L2-discrepancy
        
        
    /** <p>The L^2-discrepancy of the first N points.
     *  This computes the L^2 discrepancy T_{N+1} of the first N+1 points 
     *  r[j][], j=0,...,N from the discrepancy x=T_N of the first N points
     *  based on the recursion followed by the quantities h_N=(NT_N)^2/2.</p> 
     *
     * <p>Use this method if the L2-discrepancy is to be computed for all
     * n=1,2,3,... (number of points).</p>

     *
     * @param N number of points.
     * @param r array of points r[j][], j=0,...,N.
     * @param x discrepancy T_N.
     */
   
     public double L2_discrepancy(int N, double[][] r, double x)
     {
        
        // compute a=1/2^d, b=1/3^d
        double a=1, b=1;
        for(int k=0;k<dim;k++){ a/=2.0; b/=3.0; }
               
        if(N==0){ double two_h_1=product(0,r)-2*a*productSQ(0,r)+b;
                  return Math.sqrt(two_h_1); }
        
        
        double f=N*x,
               h_N=0.5*f*f,       
               h;             // h_{N+1}             

        // h=h_{N+1} from x=h_N
        h=h_N;
        for(int i=0;i<N;i++)h+=product(i,N,r);
        h+=0.5*product(N,r);
        
        double sum=0;
        for(int i=0;i<N;i++)sum+=productSQ(i,r);
        sum+=(N+1)*productSQ(N,r);
        sum*=a;

        h-=sum;
        h+=(N+0.5)*b;
        
        return Math.sqrt(2*h)/(N+1);
     
     } // end L2-discrepancy
     
     

/*******************************************************************************
 
        TRANSFORM UNIFORM -> MULTINORMAL
 
*******************************************************************************/
     
     /** The transform of the next uniform point in the sequence to a
      *  quasinormal vector. Method of transform: coordinatewise inverse
      *  normal CDF.</p>
      *
      */
//     public double[] nextQuasiNormalVector()
//     {
//         x=nextPoint();
//         for(int k=0;k<dim;k++)z[k]=FinMath.N_Inverse(x[k]);
//         return z;
//     }
     
/*******************************************************************************
 
        PLOT OF PROJECTIONS ON COORDINATES (i,j)
 
*******************************************************************************/
     
     /**<p>JFrame capable of drawing the projections of the sequence on
      * any pair of dimensions (i,j) (axes parallel two dimensional plane).</p>
      *
      * @param i dimension.
      * @param j dimension.
      * @param nPoints number of points to be plotted.
      */
//     public ProjectionPlot2D projectionPlot(int i, int j, int nPoints)
//     { 
//         restart();
//         String name=getName();
//         ProjectionPlot2D plot=new ProjectionPlot2D(name,i,j,nPoints){
//             
//             public double[] X(int n){ return nextPoint(); }
//          
//         }; // end plot
//         
//         return plot;
//      
//     } // end projectionPlot
     
     

} // end LowDiscrepancySequence
