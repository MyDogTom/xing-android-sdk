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

package com.xing.android.sdk;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.JsonReader;
import com.squareup.moshi.Moshi;
import com.squareup.moshi.Types;
import com.squareup.okhttp.ResponseBody;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.List;

import okio.BufferedSource;

import static com.xing.android.sdk.Utils.closeQuietly;
import static com.xing.android.sdk.Utils.ioError;

/**
 * Defines the type of responses, while adding the possibility to add a root, as well as defining if the object is a
 * list or just a single object.
 * The {@link CompositeType} can be used when defining the response type when building the CallSpec for a Resource.
 * <p>
 * This can be used as follows:
 * <pre>
 * {@code
 *  CompositeType delegate = CompositeType.single(YourReturnObject.class, [Root or List of roots where your object can
 * be
 *  found])
 *  // or for a list
 *  CompositeType delegate = CompositeType.list(YourReturnObject.class, [Root or List of roots where your object can be
 *  found])
 *
 * //Example: Usage inside the UserProfilesResource
 * public CallSpec<XingUser, Object> getUsersById(String id) {
 *  return this.<XingUser, Object>newGetSpec("/v1/users/{id}")
 *      .pathParam("id", id)
 *      .responseAsList(XingUser.class, "users")
 *      .build();
 * }
 * }
 *
 * </pre>
 *
 * @author daniel.hartwich
 * @author serj.lotutovici
 */
final class CompositeType {
    private final Type classType;
    private final String[] roots;
    private final Structure structure;

    private CompositeType(Type classType, Structure structure, String... roots) {
        this.classType = classType;
        this.structure = structure;
        this.roots = roots;
    }

    /**
     * Returns a {@link CompositeType} that will expect a single object in the root tree.
     */
    public static CompositeType single(Class<?> classType, String... roots) {
        return new CompositeType(classType, Structure.SINGLE, roots);
    }

    /**
     * Returns a {@link CompositeType} that will expect a list of objects in the root tree.
     */
    public static CompositeType list(Type classType, String... roots) {
        return new CompositeType(classType, Structure.LIST, roots);
    }

    public static CompositeType first(Type classType, String... roots) {
        return new CompositeType(classType, Structure.FIRST, roots);
    }

    /**
     * Recursive method that goes through the JSON, finds the given root and returns the objects with the provided
     * roots.
     */
    @Nullable
    private static <T> T readRootLeafs(@NonNull JsonAdapter<T> adapter, @NonNull JsonReader reader,
          @NonNull String[] roots, int index) throws IOException {
        if (index == roots.length) {
            return adapter.fromJson(reader);
        } else {
            reader.beginObject();
            try {
                String root = roots[index];
                while (reader.hasNext()) {
                    if (reader.nextName().equals(root)) {
                        if (reader.peek() == JsonReader.Token.NULL) {
                            return reader.nextNull();
                        }

                        return readRootLeafs(adapter, reader, roots, ++index);
                    } else {
                        reader.skipValue();
                    }
                }
            } finally {
                try {
                    reader.endObject();
                } catch (IOException ioe) {
                    // Ignore, in case we are closing early.
                }
            }
            throw ioError("Json does not match expected structure for roots %s.", Arrays.asList(roots));
        }
    }

    /**
     * Parses the ResponseBody and closes it. This method goes through provided roots to retrieve the object.
     */
    @Nullable
    <T> T fromJson(Moshi moshi, @Nullable ResponseBody body) throws IOException {
        if (body == null) return null;

        // Determine which type is required.
        Type type = structure == Structure.SINGLE ? classType : Types.newParameterizedType(List.class, classType);

        BufferedSource source = body.source();
        JsonAdapter<T> adapter = moshi.adapter(type);
        try {
            Object result;
            if (roots != null && roots.length != 0) {
                result = readRootLeafs(adapter, JsonReader.of(source), roots, 0);
            } else {
                result = adapter.fromJson(source);
            }

            if (structure == Structure.FIRST) {
                //noinspection unchecked This is puts full responsibility on the caller.
                List<T> list = (List<T>) result;
                return list != null && !list.isEmpty() ? list.get(0) : null;
            }

            //noinspection unchecked
            return (T) result;
        } finally {
            closeQuietly(body);
        }
    }

    /**
     * This enum defines the possible response types a user can expect.
     * These are:
     * SINGLE - A single object
     * LIST - A list of objects
     * FIRST - A single object wrapped in a list structure (happens often in profile resources)
     */
    enum Structure {
        SINGLE,
        LIST,
        FIRST
    }
}
