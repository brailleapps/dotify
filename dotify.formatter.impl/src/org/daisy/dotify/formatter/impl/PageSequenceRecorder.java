package org.daisy.dotify.formatter.impl;

import java.util.HashMap;
import java.util.Map;

import org.daisy.dotify.api.formatter.RenderingScenario;

class PageSequenceRecorder {
	private final static String baseline = "base";
	private final static String scenario = "best";
	
	PageSequenceRecorderData data;

	private RenderingScenario current = null;
	private RenderingScenario invalid = null;
	private double cost = 0;
	private float height = 0;
	private float minWidth = 0;
	private Map<String, PageSequenceRecorderData> states;

	PageSequenceRecorder() {
		data = new PageSequenceRecorderData();
		states = new HashMap<>();
	}
	
	private void saveState(String id) {
		states.put(id, new PageSequenceRecorderData(data));
	}
	
	private void restoreState(String id) {
		PageSequenceRecorderData state = states.get(id);
		if (state!=null) {
			data = new PageSequenceRecorderData(state);
		}
	}
	
	private void clearState(String id) {
		states.remove(id);
	}
	
	/**
	 * Process a new block for a scenario
	 * @param g
	 * @param rec
	 */
	AbstractBlockContentManager processBlock(Block g, BlockContext context) {
		AbstractBlockContentManager ret = g.getBlockContentManager(context);
		if (g.getRenderingScenario()!=null) {
			if (invalid!=null) {
				if (g.getRenderingScenario()==invalid) {
					return ret;
				} else {
					invalid = null;
				}
			}
			if (current==null) {
				height = data.calcSize();
				cost = Double.MAX_VALUE;
				minWidth = ret.getMinimumAvailableWidth();
				clearState(scenario);
				saveState(baseline);
				current = g.getRenderingScenario();
			} else {
				minWidth = Math.min(minWidth, ret.getMinimumAvailableWidth());
				if (current!=g.getRenderingScenario()) {
					//TODO: measure, evaluate
					float size = data.calcSize()-height;
					double ncost = current.calculateCost(setParams(size, minWidth));
					if (ncost<cost) {
						//if better, store
						cost = ncost;
						saveState(scenario);
					}
					restoreState(baseline);
					current = g.getRenderingScenario();
				} // we're rendering the current scenario
			}
		} else {
			finishBlockProcessing();
		}
		return ret;
	}
	
	void finishBlockProcessing() {
		if (current!=null) {
			//if not better
			float size = data.calcSize()-height;
			double ncost = current.calculateCost(setParams(size, minWidth));
			if (ncost>cost) {
				restoreState(scenario);
			}
			current = null;
			invalid = null;
		}
	}
	
	/**
	 * Invalidates the current scenario, if any. This causes the remainder of the
	 * scenario to be excluded from further processing.
	 * 
	 * @param e the exception that caused the scenario to be invalidated
	 * @throws RuntimeException if no scenario is active 
	 */
	void invalidateScenario(Exception e) {
		if (current==null) {
			throw new RuntimeException(e);
		} else {
			restoreState(baseline);
			current = null;
			invalid = current;
		}
	}

	private Map<String, Double> setParams(double height, double minBlockWidth) {
		Map<String, Double> params = new HashMap<>();
		params.put("total-height", height);
		params.put("min-block-width", minBlockWidth);
		return params;
	}

}
