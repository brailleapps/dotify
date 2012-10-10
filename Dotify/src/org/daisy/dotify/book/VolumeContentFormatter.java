package org.daisy.dotify.book;

import java.util.List;

import org.daisy.dotify.formatter.CrossReferences;
import org.daisy.dotify.paginator.BlockSequence;

public interface VolumeContentFormatter {
	
	public int getVolumeMaxSize(int volumeNumber, int volumeCount);
	
	public List<Iterable<BlockSequence>> formatPreVolumeContents(int volumeNumber, int volumeCount, CrossReferences crh);
	
	public List<Iterable<BlockSequence>> formatPostVolumeContents(int volumeNumber, int volumeCount, CrossReferences crh);
	
}
