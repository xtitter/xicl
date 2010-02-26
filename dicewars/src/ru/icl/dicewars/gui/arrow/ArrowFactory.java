package ru.icl.dicewars.gui.arrow;


public class ArrowFactory {

	public enum ArrowType {
		LINE,
		WITH_ARROWHEAD,
		BEZIER
	};
	
	public static Arrow getArrow(ArrowType arrowType) {
		if (arrowType.equals(ArrowType.WITH_ARROWHEAD)){
			return new LineArrowWithArrowHead();
		} else if (arrowType.equals(ArrowType.BEZIER)){ 
			return new BezierArrow();
		} else {
			return new LineArrow();
		}
	}

	private static final long serialVersionUID = 1L;
}
