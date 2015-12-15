/*
 * Copyright (c) 2015 XING AG (http://xing.com/)
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package com.xing.android.sdk.model.user;

import android.os.Parcel;
import android.os.Parcelable;

import com.squareup.moshi.Json;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents a users educational background.
 *
 * @author serj.lotutovici
 * @see <a href="https://dev.xing.com/docs/get/users/:id">User Profile</a>
 */
public class EducationalBackground implements Serializable, Parcelable {
    /** Creator object for the Parcelable contract. */
    public static final Creator<EducationalBackground> CREATOR = new Creator<EducationalBackground>() {
        @Override
        public EducationalBackground createFromParcel(Parcel source) {
            return new EducationalBackground(source);
        }

        @Override
        public EducationalBackground[] newArray(int size) {
            return new EducationalBackground[size];
        }
    };
    private static final long serialVersionUID = 2927900492592865436L;
    /** Educational degree. */
    @Json(name = "degree")
    private String degree;
    /** Primary school attended. */
    @Json(name = "primary_school")
    private School primarySchool;
    /** List of schools attended. */
    @Json(name = "schools")
    private List<School> schools;
    /** List of qualifications. */
    @Json(name = "qualifications")
    private List<String> qualifications;

    /** Create a simple Educational Background object with empty fields. */
    public EducationalBackground() {
    }

    /**
     * Create {@link EducationalBackground} from {@link Parcel}.
     *
     * @param in Input {@link Parcel}
     */
    private EducationalBackground(Parcel in) {
        degree = in.readString();
        primarySchool = in.readParcelable(School.class.getClassLoader());
        schools = new ArrayList<>(1);
        in.readTypedList(schools, School.CREATOR);
        qualifications = new ArrayList<>(1);
        in.readStringList(qualifications);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof EducationalBackground)) {
            return false;
        }

        EducationalBackground educationalBackground = (EducationalBackground) o;
        return hashCode() == educationalBackground.hashCode();
    }

    @Override
    public int hashCode() {
        int result = degree != null ? degree.hashCode() : 0;
        result = 31 * result + (primarySchool != null ? primarySchool.hashCode() : 0);
        result = 31 * result + (schools != null ? schools.hashCode() : 0);
        result = 31 * result + (qualifications != null ? qualifications.hashCode() : 0);
        return result;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(degree);
        dest.writeParcelable(primarySchool, 0);
        dest.writeTypedList(schools);
        dest.writeStringList(qualifications);
    }

    /**
     * Return degree.
     *
     * @return degree.
     */
    public String getDegree() {
        return degree;
    }

    /**
     * Set degree.
     *
     * @param degree Set degree.
     */
    public void setDegree(String degree) {
        this.degree = degree;
    }

    /**
     * Return primary school.
     *
     * @return primary school.
     */
    public School getPrimarySchool() {
        return primarySchool;
    }

    /**
     * Set primary school.
     *
     * @param primarySchool primary school.
     */
    public void setPrimarySchool(School primarySchool) {
        this.primarySchool = primarySchool;
    }

    /**
     * Return list of schools.
     *
     * @return list of schools.
     */
    public List<School> getSchools() {
        return schools;
    }

    /**
     * Set list of schools.
     *
     * @param mSchools list of schools.
     */
    public void setSchools(List<School> mSchools) {
        schools = mSchools;
    }

    /**
     * Return list of qualifications.
     *
     * @return list of qualifications.
     */
    public List<String> getQualifications() {
        return qualifications;
    }

    /**
     * Set list of qualifications.
     *
     * @param qualifications list of qualifications.
     */
    public void setQualifications(List<String> qualifications) {
        this.qualifications = qualifications;
    }
}
