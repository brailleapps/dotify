package org.daisy.dotify.formatter.dom;

import org.daisy.dotify.formatter.utils.Position;

/**
 * <p>Positions a block of text at a specified row.</p>
 * @author Joel Håkansson, TPB
 */
class BlockPosition {
	/**
	 * Block alignment
	 */
	public enum Alignment {
		/**
		 * Aligns the first row of the block at the given position
		 */
		TOP,
		/**
		 * Aligns the last row of the block at the given position
		 */
		BOTTOM,
		/**
		 * Centers the block at the given position (round off towards the top)
		 */
		CENTER};
	private final Position position;
	private final Alignment align;
	
	/**
	 * Used when creating BlockPosition instances.
	 * @author Joel Håkansson, TPB
	 *
	 */
	public static class Builder {
		// optional
		private Alignment align;
		private Position pos;
		
		/**
		 * Creates a new Builder with TOP alignment at the absolute position of 0. 
		 */
		public Builder() {
			this.align = Alignment.TOP;
			this.pos = new Position(0, false);
		}

		/**
		 * Sets the vertical position of the block, 0 is the first row.
		 * @param pos the position where the block should be positioned
		 * @return returns this Builder
		 */
		public Builder position(Position pos) {
			this.pos = pos;
			return this;
		}
		
		/**
		 * Sets the vertical alignment of the block, that is to say the row within the block that will be at the specified position. 
		 * @param align the alignment
		 * @return returns this Builder
		 */
		public Builder align(Alignment align) {
			this.align = align;
			return this;
		}
		
		/**
		 * Creates a new BlockPosition instance
		 * @return returns a new BlockPosition instance
		 */
		public BlockPosition build() {
			return new BlockPosition(this);
		}
	}
	
	private BlockPosition(Builder builder) {
		this.position = builder.pos;
		this.align = builder.align;
	}

	/**
	 * Gets the row for the block that this object positions. 0 is the first row.
	 * @return returns the position
	 */
	public Position getPosition() {
		return position;
	}
	
	/**
	 * Get the alignment center for the block that this object positions
	 * @return returns the alignment center
	 */
	public Alignment getAlignment() {
		return align;
	}

}
