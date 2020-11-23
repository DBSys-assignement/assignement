
package bufmgr;

import diskmgr.*;
import global.*;

//Since it's similar to FIFO.java, I reuse it from FIFO.java and modify it.
class LIFO extends Replacer{

    private int  frames[];
    private int  nframes;

    //Similar the FIFO and LRU, but push the frame to the beginning but last.
    private void update(int frameNo)
    {

        int index;
        for ( index=0; index < nframes; ++index )
          if ( frames[index] == frameNo )
              break;

        //It's already in the beginning
        if(index == 0){
          return;
        }
        //Move frames[i - 1] to frames[i] to empty the first frame.
        for(int i = index; i > 0; --i){
            frames[i] = frames[i - 1];
        }
        //Then move the frame to the beginning.
        frames[0] = frameNo;
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

    //copy from LRU.java!!
    /* public methods */
    /**
    * Class constructor
    * Initializing frames[] pinter = null.
    */
    public LIFO(BufMgr mgrArg)
    {
        super(mgrArg);
        frames = null;
    }

    //copy from LRU.java!!
    /**
    * calll super class the same method
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
    throws BufferPoolExceededException
    {
        //copy from LRU.java
        //If the buffer isn't full yet, pick the last one.
        int numBuffers = mgr.getNumBuffers();
        int frame;
        if ( nframes < numBuffers ) {
            frame = nframes++;
            frames[frame] = frame;
            state_bit[frame].state = Pinned;
            (mgr.frameTable())[frame].pin();
            update(frame);
            return frame;
        }
        //It's similar to FIFO, pick the first frame isn't pinned.
        //Then modify the update function, put the newest frame in the beginning.
        //Cope from LRU.java and modify it.
        for(int i = 0; i < numBuffers; ++i){
            frame = frames[i];
            if (state_bit[frame].state != Pinned){
                state_bit[frame].state = Pinned;
                (mgr.frameTable())[frame].pin();
                update(frame);
                return frame;
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
    public String name() { return "LIFO"; }

    /**
    * print out the information of frame usage
    */
    public void info()
    {
        super.info();
        System.out.print( "LIFO REPLACEMENT");
        for (int i = 0; i < nframes; i++) {
            if (i % 5 == 0)
                System.out.println( );
            System.out.print( "\t" + frames[i]);
        }
        System.out.println();
    }
}
