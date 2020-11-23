package bufmgr;

import diskmgr.*;
import global.*;
import java.util.*;

//Except the pick_victim() function, other parts are similar to other replacers.
//Therefore, I reuse it from LRU.java.
public class LRUK extends Replacer{
	
	

	// As normal LRU
    private int  frames[];
    private int  nframes;
    private int K;
    int PageId;

    // History of pages access
    private HashMap<Integer, Long[]> HIST;
    private HashMap<Integer, Long> Last;
    private long Correlated_Reference_Period = 0;

    // For the Correlated period
    int ReferencePeriod = 0;
    // Can be setted we configeration 
    public void ReferencePeriodSet(int Period){
    	ReferencePeriod = Period;
    }

    /**
    * This pushes the given frame to the end of the list.
    * @param frameNo	the frame number
    */

    Boolean inBuffer = false;
    Long histoies[];

    private void update(int frameIndex)
    {
    	
    	//for the case that the page isn't inside the buffer
    	if (inBuffer == false) {
    		if(HIST.containsKey(PageId)){
    			histoies = HIST.get(PageId);
    			for (int i = 1; i<K; i++) {
    				histoies[i] = histoies[i-1];
    			}

    		}else {
    			histoies = new Long[K];
    			Arrays.fill(histoies, 0L);
    		}
    		histoies[0] = System.currentTimeMillis();
			HIST.put(PageId, histoies);
			Last.put(PageId, histoies[0]);
    	}else {
    		//for the case that page is inside the buffer

    		PageId = frames[frameIndex];
    		long LastTime = Last.get(PageId);
    		if ((System.currentTimeMillis() - LastTime) >= Correlated_Reference_Period) {
    			long correl_period_of_refd_page = LastTime - HIST.get(PageId)[0];
    			histoies = HIST.get(PageId);
    			for (int i = 1; i<K; i++) {
    				histoies[i] = histoies[i-1] + correl_period_of_refd_page;
    			}
    			histoies[0] = System.currentTimeMillis();
    			HIST.put(PageId, histoies);
    			Last.put(PageId, histoies[0]);
    		}else {
    			Last.put(PageId, System.currentTimeMillis());
    		}

    	}
    }


    //copy from LRU.java!!
    /**
    * Calling super class the same method
    * Initializing the frames[] with number of buffer allocated
    * by buffer manager
    * set number of frame used to zero
    *
    * @param	mgr	a BufMgr object
    * @see	BufMgr
    * @see	Replacer
    */
    public void setBufferManager( BufMgr mgr )
    {
        super.setBufferManager(mgr);
        frames = new int [ mgr.getNumBuffers() ];
        nframes = 0;
    }


    
    /**
    * Class constructor
    * Initializing frames[] pinter = null.
    */
    public LRUK(BufMgr mgrArg, int _K)
    {
        super(mgrArg);
        frames = null;
        inBuffer = false;
        K = _K;
        PageId = -1;
        HIST = new HashMap<Integer, Long[]>();
        Last = new HashMap<Integer, Long>();
    }

    /**
    * call super class the same method
    * pin the page in the given frame number
    * move the page to the end of list
    *
    * @param	 frameNo	 the frame number to pin
    * @exception  InvalidFrameNumberException
    */
    public void pin(int frameNo) throws InvalidFrameNumberException
    {
        super.pin(frameNo);
        update(frameNo);
    }

    /**
    * Finding a free frame in the buffer pool
    * or choosing a page to replace using LRU policy
    *
    * @return 	return the frame number
    *		return -1 if failed
    */
    public int pick_victim()
   		 throws BufferPoolExceededException, PagePinnedException 
    {
        int numBuffers = mgr.getNumBuffers();
        int frameIndex = -1;
        int victim = -1;

        if ( nframes < numBuffers ) {
         // buffer is not full
            frameIndex = nframes++;
            frames[frameIndex] = PageId;
            state_bit[frameIndex].state = Pinned;
            if ( state_bit[frameIndex].state != Pinned ) {
            	System.out.print("YES");
            }
//            System.out.println(state_bit[frameIndex].state);
            (mgr.frameTable())[frameIndex].pin();
            update(frameIndex);
            return frameIndex;
        }

       // buffer is full

       int Index;
       long timeMin = System.currentTimeMillis();
//       System.out.println(numBuffers);
//       for ( int j = 0; j < numBuffers; ++j ) {
//		   System.out.println(state_bit[j].state);
//		   if (state_bit[j].state != Pinned ) {
//			   System.out.println("YEAN");
//		   }else {
//			   System.out.println("No");
//			   
//		   }
//		   System.out.println(state_bit[j].state);
//	   }
       
       for ( int i = 0; i < numBuffers; ++i ) {
    	   Index = frames[i];
//    	   System.out.println(Index);
//    	   System.out.println(i);
    	   
           if ( state_bit[i].state != Pinned ) {
//        	   System.out.println(i);
//        	   System.out.println('\n');
        	   
        	   // Find the shortest K reference 
        	   if((System.currentTimeMillis() - Last.get(Index))>= Correlated_Reference_Period 
        			   && HIST.get(Index)[(K-1)] <= timeMin){
        		   victim = Index;
                   frameIndex = i;
                   timeMin =  HIST.get(Index)[(K-1)];
        	   }

           }
           if (frameIndex>=0){
           state_bit[frameIndex].state = Pinned;
           (mgr.frameTable())[frameIndex].pin();
           update(frameIndex);
           return frameIndex;
         }
       }
      

   	  throw new BufferPoolExceededException (null, "BUFMGR: BUFFER_EXCEEDED.");
    }


    //copy from LRU.java!!
    /**
    * get the page replacement policy name
    *
    * @return	return the name of replacement policy used
    */
    public String name() { return "LRUK"; }

    /**
    * print out the information of frame usage
    */
    public void info()
    {
        super.info();
        System.out.print( "LRUK REPLACEMENT");
        for (int i = 0; i < nframes; i++) {
            if (i % 5 == 0)
                System.out.println( );
            System.out.print( "\t" + frames[i]);
        }
        System.out.println();
    }


	public int[] getFrames() {
		// TODO Auto-generated method stub
		return frames;
	}


	public Long last(int pagenumber) {
		// TODO Auto-generated method stub
		if (state_bit[pagenumber] .state != Pinned) {
			return Last.get(pagenumber);
		}
		return (long) -1;
	}


	public long HIST(int pagenumber, int i) {
		// TODO Auto-generated method stub
		if (HIST.containsKey(pagenumber)) {
			return HIST.get(pagenumber)[i];
		}
		return -1;
	}
}
