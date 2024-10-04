import pl.borek497.bookstore.demo.HorsesFarm
import pl.borek497.bookstore.users.application.UserService
import spock.lang.Specification


class ExampleSpecification extends Specification {

    def "Should be a simple assertion"() {
        expect:
        1 == 1
    }

    def "Should demonstrate given-when-then"() {
        given:
        var farm = new HorsesFarm(10);

        when:
        int horses = farm.numberOfHorses;

        then:
        horses == 10;
    }

    def "Should expect exception"() {
        when:
        new HorsesFarm(0);

        then:
        thrown(RuntimeException)
    }

    def "Should be able to create horse farm with #horses horses"() {
        expect:
        new HorsesFarm(horses).numberOfHorses == horses;

        where:
        horses << [11, 16, 80, 10000]
    }

    def "Should use data tables for calculating max. Max of #a and #b is #max"() {
        expect:
        Math.max(a, b) == max;

        where:
        a | b || max
        1 | 3 || 3
        7 | 4 || 7
        0 | 0 || 0
    }

    def "Should be able to mock a concrete class"() {
        given:
        //two ways of declare mock
        UserService userService = Mock();
        def userService2 = Mock(UserService)
    }
}