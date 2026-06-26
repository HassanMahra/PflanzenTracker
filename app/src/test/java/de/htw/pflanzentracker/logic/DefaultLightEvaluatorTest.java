package de.htw.pflanzentracker.logic;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import de.htw.pflanzentracker.model.LightRequirement;

public class DefaultLightEvaluatorTest {

    private LightEvaluator evaluator = new DefaultLightEvaluator();

    @Test
    public void tooDark() {
        assertEquals(LightRating.TOO_DARK, evaluator.evaluate(10f, LightRequirement.LOW));
    }

    @Test
    public void ok() {
        assertEquals(LightRating.OK, evaluator.evaluate(500f, LightRequirement.LOW));
    }

    @Test
    public void tooBright() {
        assertEquals(LightRating.TOO_BRIGHT, evaluator.evaluate(5000f, LightRequirement.LOW));
    }
}
