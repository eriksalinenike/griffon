package org.codehaus.griffon.runtime.shiro

import com.acme.ShiroController
import com.acme.ShiroModel
import griffon.core.artifact.ArtifactManager
import griffon.core.test.GriffonUnitRule
import griffon.core.test.TestFor
import org.apache.shiro.authc.UsernamePasswordToken
import org.apache.shiro.subject.Subject
import org.junit.Rule
import spock.lang.Specification
import spock.lang.Unroll

import javax.inject.Inject

@TestFor(ShiroController)
class ShiroSpec extends Specification {
    static {
        System.setProperty('org.slf4j.simpleLogger.defaultLogLevel', 'trace')
    }

    @Rule
    final GriffonUnitRule griffon = new GriffonUnitRule()

    @Inject
    private Subject subject

    @Inject
    private ArtifactManager artifactManager

    private ShiroController controller

    @Unroll
    def 'Guest with #action has outcome #outcome'() {
        given:
        controller.model = artifactManager.newInstance(ShiroModel)

        when:
        controller.invokeAction(action)

        then:
        controller.model.value == outcome

        where:
        action          | outcome
        'guest'         | 'guest'
        'user'          | null
        'authenticated' | null
        'roles'         | null
        'permissions'   | null
    }

    @Unroll
    def 'User #user with #action has outcome #outcome'() {
        given:
        controller.model = artifactManager.newInstance(ShiroModel)
        subject.login(new UsernamePasswordToken(user, user))

        when:
        controller.invokeAction(action)

        then:
        controller.model.value == outcome

        cleanup:
        subject.logout()

        where:
        user   | action          | outcome
        'bob'  | 'guest'         | null
        'bob'  | 'user'          | null
        'bob'  | 'authenticated' | 'authenticated'
        'bob'  | 'roles'         | 'roles'
        'bob'  | 'permissions'   | 'permissions'
        'joe'  | 'guest'         | null
        'joe'  | 'user'          | null
        'joe'  | 'authenticated' | 'authenticated'
        'joe'  | 'roles'         | null
        'joe'  | 'permissions'   | null
        'root' | 'guest'         | null
        'root' | 'user'          | null
        'root' | 'authenticated' | 'authenticated'
        'root' | 'roles'         | 'roles'
        'root' | 'permissions'   | 'permissions'
    }
}
