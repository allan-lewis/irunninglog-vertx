package com.irunninglog.spring.profile.impl;

import com.irunninglog.api.Gender;
import com.irunninglog.api.Unit;
import com.irunninglog.spring.AbstractTest;
import org.junit.Test;

import java.time.DayOfWeek;
import java.time.LocalDate;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class ProfileEntityTest extends AbstractTest {

    @Test
    public void sanity() {
        ProfileEntity entity = new ProfileEntity();
        entity.setId(1);
        entity.setFirstName("Allan");
        entity.setLastName("Lewis");
        entity.setBirthday(LocalDate.now());
        entity.setGender(Gender.Male);
        entity.setEmail("allan@irunninglog.com");
        entity.setPassword("foo");
        entity.setWeekStart(DayOfWeek.MONDAY);
        entity.setPreferredUnits(Unit.English);
        entity.setWeeklyTarget(25);
        entity.setMonthlyTarget(125);
        entity.setYearlyTarget(1500);

        assertEquals(1, entity.getId());
        assertEquals("Allan", entity.getFirstName());
        assertEquals("Lewis", entity.getLastName());
        assertEquals("allan@irunninglog.com", entity.getEmail());
        assertEquals("foo", entity.getPassword());
        assertEquals(Gender.Male, entity.getGender());
        assertEquals(DayOfWeek.MONDAY, entity.getWeekStart());
        assertEquals(Unit.English, entity.getPreferredUnits());
        assertEquals(25, entity.getWeeklyTarget(), 1E-9);
        assertEquals(125, entity.getMonthlyTarget(), 1E-9);
        assertEquals(1500, entity.getYearlyTarget(), 1E-9);
        assertNotNull(entity.getBirthday());
    }

}
