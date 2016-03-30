package com.badoo.barf.data.repo;

import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class BaseRepoTest {

    @Test
    public void assertQueryOverridesEqualsAndHashcodeWithBothDefined() {
        assertThat(BaseRepository.assertQueryOverridesEqualsAndHashcode(WithEqAndHashCode.class), is(true));
    }

    @Test
    public void assertQueryOverridesEqualsAndHashcodeWithEqDefined() {
        assertThat(BaseRepository.assertQueryOverridesEqualsAndHashcode(WithoutEq.class), is(false));
    }

    @Test
    public void assertQueryOverridesEqualsAndHashcodeWithHashCodeDefined() {
        assertThat(BaseRepository.assertQueryOverridesEqualsAndHashcode(WithoutHashCode.class), is(false));
    }

    @Test
    public void assertQueryOverridesEqualsAndHashcodeWithNeitherDefined() {
        assertThat(BaseRepository.assertQueryOverridesEqualsAndHashcode(WithoutEqAndHashCode.class), is(false));
    }


    private final class WithEqAndHashCode {
        @Override
        public boolean equals(Object o) {
            return super.equals(o);
        }

        @Override
        public int hashCode() {
            return super.hashCode();
        }
    }

    private final class WithoutEq {
        @Override
        public int hashCode() {
            return super.hashCode();
        }
    }

    private final class WithoutHashCode {
        @Override
        public boolean equals(Object o) {
            return super.equals(o);
        }
    }

    private final class WithoutEqAndHashCode {}
}
