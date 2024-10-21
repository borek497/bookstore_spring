import spock.lang.Specification


class UnderstandSpockTest extends Specification {

    def "should correctly add two numbers"() {
        expect:
        add(2, 3) == 5
    }

    def "should return correct value. When add #a and #b then result is #result "() {
        expect:
        add(a, b) == result

        where:
        a     | b     || result
        124   | 267   || 391
        345   | 897   || 1242
        999   | 10654 || 11653
        12785 | 23489 || 36274
    }

    def "should throw IllegalArgumentException when input is negative"() {
        when:
        validateNumber(-1)

        then:
        thrown(IllegalArgumentException)
    }

//    def "test z mockowaniem"() {
//        given:
//        def mockService = Mock("serviceClassName")
//
//        mockService.getData() >> "wynik mocka"
//        mockService.getData() >> "pierwszy wynik" >> "drugi wynik"
//        mockService.getData() >> { throw new IllegalArgumentException("Błąd mocka") }
//
//        when:
//        def result1 = mockService.getData()
//        def result2 = mockService.getData()
//        def result3 = mockService.getData()
//
//        then:
//        result1 == "pierwszy wynik"
//        result2 == "drugi wynik"
//        result3 == thrown(IllegalArgumentException)
//    }

    def "dynamiczny test z mockowaniem"() {

        given:
        def mockService = Mock("ServiceName")

        mockService.calculateValue(_) >> {int x -> x * 2}
        //(_) symbol zastępczy, nie obchodzi mnie konkretny argument pzekazany do danej metody mocka - akceptujemy dowolny
        //parametr. Metoda powinna działać niezależnie od przekazanego argumentu

        when:
        def result1 = mockService.calculateValue(5)
        def result2 = mockService.calculateValue(10)

        then:
        result1 == 10
        result2 == 20
    }

    def "test wildcard"() {

    }


    int add(int a, int b) {
        return a + b;
    }

    void validateNumber(int number) {
        if (number < 0) {}
        throw new IllegalArgumentException("Give number is negative")
    }
}