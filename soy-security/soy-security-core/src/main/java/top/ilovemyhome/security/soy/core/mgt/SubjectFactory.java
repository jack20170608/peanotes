package top.ilovemyhome.security.soy.core.mgt;


import top.ilovemyhome.security.soy.core.subject.Subject;
import top.ilovemyhome.security.soy.core.subject.SubjectContext;

import java.util.Map;

/**
 * A {@code SubjectFactory} is responsible for constructing {@link Subject Subject} instances as needed.
 *
 * @since 1.0
 */
@FunctionalInterface
public interface SubjectFactory {

    /**
     * Creates a new Subject instance reflecting the state of the specified contextual data.  The data would be
     * anything required to required to construct a {@code Subject} instance and its contents can vary based on
     * environment.
     * <p/>
     * Any data supported by Shiro core will be accessible by one of the {@code SubjectContext}'s {@code get*}
     * or {@code resolve*} methods.  All other data is available as map {@link Map#get attribute}s.
     *
     * @param context the contextual data to be used by the implementation to construct an appropriate {@code Subject}
     *                instance.
     * @return a {@code Subject} instance created based on the specified context.
     * @see SubjectContext
     */
    Subject createSubject(SubjectContext context);

}
