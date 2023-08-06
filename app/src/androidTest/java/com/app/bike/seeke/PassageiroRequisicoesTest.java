package com.app.bike.seeke;

import com.app.bike.seeke.view.activitys.PassageiroActivity;

import org.junit.Test;

import java.lang.annotation.Annotation;

public class PassageiroRequisicoesTest implements Test {

    private PassageiroActivity passageiroActivity;

    public PassageiroActivity getPassageiroActivity() {
        return passageiroActivity;
    }

    @Override
    public Class<? extends Throwable> expected() {
        return null;
    }

    @Override
    public long timeout() {
        return 0;
    }

    @Override
    public boolean equals(Object obj) {
        return false;
    }

    @Override
    public int hashCode() {
        return 0;
    }

    @Override
    public String toString() {
        return null;
    }

    @Override
    public Class<? extends Annotation> annotationType() {
        return null;
    }
}
