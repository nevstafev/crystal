package services;

import domain.Price;
import org.junit.Before;
import org.junit.Test;

import java.text.SimpleDateFormat;
import java.util.*;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.*;

public class PriceServiceTest {

    private PriceService priceService;
    private SimpleDateFormat simpleDateFormat;

    @Before
    public void setUp() {
        priceService = new PriceService();
        simpleDateFormat = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
    }

    @Test
    public void testGetPricesPeriodDifferenceWithForwardOffset() throws Exception {
        Price existedPrice = new Price("productCode", 1, 1, simpleDateFormat.parse("10.01.2013 00:00:00"), simpleDateFormat.parse("20.01.2013 23:59:59"), 99000);

        Price newPrice = new Price("productCode", 1, 1, simpleDateFormat.parse("15.01.2013 00:00:00"), simpleDateFormat.parse("25.01.2013 23:59:59"), 92000);

        Price expectedPrice1 = new Price("productCode", 1, 1, simpleDateFormat.parse("10.01.2013 00:00:00"), simpleDateFormat.parse("15.01.2013 00:00:00"), 99000);

        List<Price> pricesDifferences = priceService.getPricesPeriodDifference(existedPrice, newPrice);

        assertThat(pricesDifferences.size(), is(1));
        assertTrue(pricesDifferences.containsAll(Arrays.asList(expectedPrice1)));
    }

    @Test
    public void testGetPricesPeriodDifferenceWithBackwardOffset() throws Exception {
        Price existedPrice = new Price("productCode", 1, 1, simpleDateFormat.parse("15.01.2013 00:00:00"), simpleDateFormat.parse("25.01.2013 23:59:59"), 99000);
        Price newPrice = new Price("productCode", 1, 1, simpleDateFormat.parse("10.01.2013 00:00:00"), simpleDateFormat.parse("20.01.2013 23:59:59"), 99000);

        List<Price> pricesDifferences = priceService.getPricesPeriodDifference(existedPrice, newPrice);

        Price expectedPrice1 = new Price("productCode", 1, 1, simpleDateFormat.parse("20.01.2013 23:59:59"), simpleDateFormat.parse("25.01.2013 23:59:59"), 99000);

        assertThat(pricesDifferences.size(), is(1));
        assertTrue(pricesDifferences.containsAll(Arrays.asList(expectedPrice1)));
    }

    @Test
    public void testGetPricesPeriodDifferenceWithOccurrence() throws Exception {
        Price existedPrice = new Price("productCode", 1, 1, simpleDateFormat.parse("01.01.2013 00:00:00"), simpleDateFormat.parse("31.01.2013 00:00:00"), 99000);
        Price newPrice = new Price("productCode", 1, 1, simpleDateFormat.parse("12.01.2013 00:00:00"), simpleDateFormat.parse("13.01.2013 00:00:00"), 99000);

        List<Price> pricesDifferences = priceService.getPricesPeriodDifference(existedPrice, newPrice);

        Price expectedPrice1 = new Price("productCode", 1, 1, simpleDateFormat.parse("01.01.2013 00:00:00"), simpleDateFormat.parse("12.01.2013 00:00:00"), 99000);
        Price expectedPrice2 = new Price("productCode", 1, 1, simpleDateFormat.parse("13.01.2013 00:00:00"), simpleDateFormat.parse("31.01.2013 00:00:00"), 99000);

        assertThat(pricesDifferences.size(), is(2));
        assertTrue(pricesDifferences.containsAll(Arrays.asList(expectedPrice1, expectedPrice2)));
    }

    @Test
    public void testGetPricesPeriodDifferenceWithAbsorption() throws Exception {
        Price existedPrice = new Price("productCode", 1, 1, simpleDateFormat.parse("12.01.2013 00:00:00"), simpleDateFormat.parse("13.01.2013 00:00:00"), 99000);
        Price newPrice = new Price("productCode", 1, 1, simpleDateFormat.parse("01.01.2013 00:00:00"), simpleDateFormat.parse("31.01.2013 00:00:00"), 99000);

        List<Price> pricesDifferences = priceService.getPricesPeriodDifference(existedPrice, newPrice);

        assertThat(pricesDifferences.size(), is(0));
    }

    @Test
    public void testGetPricesPeriodDifferenceWithMatchingPeriods() throws Exception {
        Price existedPrice = new Price("productCode", 1, 1, simpleDateFormat.parse("12.01.2013 00:00:00"), simpleDateFormat.parse("13.01.2013 00:00:00"), 99000);
        Price newPrice = new Price("productCode", 1, 1, simpleDateFormat.parse("12.01.2013 00:00:00"), simpleDateFormat.parse("13.01.2013 00:00:00"), 99000);

        List<Price> pricesDifferences = priceService.getPricesPeriodDifference(existedPrice, newPrice);

        assertTrue(pricesDifferences.isEmpty());
    }

    @Test
    public void testGetPricesPeriodDifferenceWithNoIntersectionsPeriods() throws Exception {
        Price existedPrice = new Price("productCode", 1, 1, simpleDateFormat.parse("12.01.2013 00:00:00"), simpleDateFormat.parse("13.01.2013 00:00:00"), 99000);
        Price newPrice = new Price("productCode", 1, 1, simpleDateFormat.parse("14.01.2013 00:00:00"), simpleDateFormat.parse("15.01.2013 00:00:00"), 99000);

        List<Price> pricesDifferences = priceService.getPricesPeriodDifference(existedPrice, newPrice);

        Price expectedPrice1 = new Price("productCode", 1, 1, simpleDateFormat.parse("12.01.2013 00:00:00"), simpleDateFormat.parse("13.01.2013 00:00:00"), 99000);

        assertThat(pricesDifferences.size(), is(1));
        assertTrue(pricesDifferences.containsAll(Arrays.asList(expectedPrice1)));
    }

    @Test
    public void testJoinPrices() throws Exception {
        Price existedPrice1 = new Price("122856", 1, 1, simpleDateFormat.parse("01.01.2013 00:00:00"), simpleDateFormat.parse("31.01.2013 23:59:59"), 11000);
        Price existedPrice2 = new Price("122856", 2, 1, simpleDateFormat.parse("10.01.2013 00:00:00"), simpleDateFormat.parse("20.01.2013 23:59:59"), 99000);
        Price existedPrice3 = new Price("6654", 1, 2, simpleDateFormat.parse("01.01.2013 00:00:00"), simpleDateFormat.parse("31.01.2013 00:00:00"), 5000);

        Price newPrice1 = new Price("122856", 1, 1, simpleDateFormat.parse("20.01.2013 00:00:00"), simpleDateFormat.parse("20.02.2013 23:59:59"), 11000);
        Price newPrice2 = new Price("122856", 2, 1, simpleDateFormat.parse("15.01.2013 00:00:00"), simpleDateFormat.parse("25.01.2013 23:59:59"), 92000);
        Price newPrice3 = new Price("6654", 1, 2, simpleDateFormat.parse("12.01.2013 00:00:00"), simpleDateFormat.parse("13.01.2013 00:00:00"), 4000);

        Price resultPrice1 = new Price("122856", 1, 1, simpleDateFormat.parse("01.01.2013 00:00:00"), simpleDateFormat.parse("20.02.2013 23:59:59"), 11000);
        Price resultPrice2 = new Price("122856", 2, 1, simpleDateFormat.parse("10.01.2013 00:00:00"), simpleDateFormat.parse("15.01.2013 00:00:00"), 99000);
        Price resultPrice3 = new Price("122856", 2, 1, simpleDateFormat.parse("15.01.2013 00:00:00"), simpleDateFormat.parse("25.01.2013 23:59:59"), 92000);
        Price resultPrice4 = new Price("6654", 1, 2, simpleDateFormat.parse("01.01.2013 00:00:00"), simpleDateFormat.parse("12.01.2013 00:00:00"), 5000);
        Price resultPrice5 = new Price("6654", 1, 2, simpleDateFormat.parse("12.01.2013 00:00:00"), simpleDateFormat.parse("13.01.2013 00:00:00"), 4000);
        Price resultPrice6 = new Price("6654", 1, 2, simpleDateFormat.parse("13.01.2013 00:00:00"), simpleDateFormat.parse("31.01.2013 00:00:00"), 5000);

        List<Price> existedPrices = Arrays.asList(existedPrice1, existedPrice2, existedPrice3);
        List<Price> newPrices = Arrays.asList(newPrice1, newPrice2, newPrice3);

        List<Price> expectedResultPrices = Arrays.asList(resultPrice1, resultPrice2, resultPrice3, resultPrice4, resultPrice5, resultPrice6);

        Collection<Price> joinedPrices = priceService.joinPrices(existedPrices, newPrices);

        assertThat(joinedPrices.size(), is(expectedResultPrices.size()));
        assertTrue(joinedPrices.containsAll(expectedResultPrices));

    }

    @Test
    public void testJoinPricesWithNoExistedPrices() throws Exception {
        Price newPrice1 = new Price("122856", 1, 1, simpleDateFormat.parse("20.01.2013 00:00:00"), simpleDateFormat.parse("20.02.2013 23:59:59"), 11000);
        Price newPrice2 = new Price("122856", 2, 1, simpleDateFormat.parse("15.01.2013 00:00:00"), simpleDateFormat.parse("25.01.2013 23:59:59"), 92000);

        List<Price> newPrices = Arrays.asList(newPrice1, newPrice2);

        Collection<Price> joinedPrices = priceService.joinPrices(Collections.emptyList(), newPrices);

        assertThat(joinedPrices.size(), is(newPrices.size()));
        assertTrue(joinedPrices.containsAll(newPrices));
    }

    @Test
    public void testJoinPricesWithoutMatchingWithExistedPrices() throws Exception {
        Price existedPrice1 = new Price("122856", 1, 1, simpleDateFormat.parse("20.01.2013 00:00:00"), simpleDateFormat.parse("20.02.2013 23:59:59"), 11000);
        Price existedPrice2 = new Price("122856", 2, 1, simpleDateFormat.parse("15.01.2013 00:00:00"), simpleDateFormat.parse("25.01.2013 23:59:59"), 92000);

        List<Price> existedPrices = Arrays.asList(existedPrice1, existedPrice2);

        Price newPrice1 = new Price("6654", 1, 1, simpleDateFormat.parse("20.01.2013 00:00:00"), simpleDateFormat.parse("20.02.2013 23:59:59"), 11000);
        Price newPrice2 = new Price("6654", 2, 1, simpleDateFormat.parse("15.01.2013 00:00:00"), simpleDateFormat.parse("25.01.2013 23:59:59"), 92000);

        List<Price> newPrices = Arrays.asList(newPrice1, newPrice2);

        Collection<Price> joinedPrices = priceService.joinPrices(existedPrices, newPrices);

        assertThat(joinedPrices.size(), is(4));
        assertTrue(joinedPrices.containsAll(existedPrices));
        assertTrue(joinedPrices.containsAll(newPrices));
    }

    @Test
    public void testJoinPricesWithOccurrencePricesRelation() throws Exception {
        Price existedPrice1 = new Price("1", 1, 1, simpleDateFormat.parse("01.01.2013 00:00:00"), simpleDateFormat.parse("30.01.2013 00:00:00"), 50);

        Price newPrice1 = new Price("1", 1, 1, simpleDateFormat.parse("05.01.2013 00:00:00"), simpleDateFormat.parse("15.01.2013 00:00:00"), 60);

        Price expectedPrice1 = new Price("1", 1, 1, simpleDateFormat.parse("01.01.2013 00:00:00"), simpleDateFormat.parse("05.01.2013 00:00:00"), 50);
        Price expectedPrice2 = new Price("1", 1, 1, simpleDateFormat.parse("05.01.2013 00:00:00"), simpleDateFormat.parse("15.01.2013 00:00:00"), 60);
        Price expectedPrice3 = new Price("1", 1, 1, simpleDateFormat.parse("15.01.2013 00:00:00"), simpleDateFormat.parse("30.01.2013 00:00:00"), 50);


        Collection<Price> joinedPrices = priceService.joinPrices(Arrays.asList(existedPrice1), Arrays.asList(newPrice1));

        assertThat(joinedPrices.size(), is(3));
        assertTrue(joinedPrices.containsAll(Arrays.asList(expectedPrice1, expectedPrice2, expectedPrice3)));
    }

    @Test
    public void testJoinPricesWithForwardAndBackWardOffsetRelation() throws Exception {
        Price existedPrice1 = new Price("1", 1, 1, simpleDateFormat.parse("01.01.2013 00:00:00"), simpleDateFormat.parse("15.01.2013 00:00:00"), 100);
        Price existedPrice2 = new Price("1", 1, 1, simpleDateFormat.parse("15.01.2013 00:00:00"), simpleDateFormat.parse("30.01.2013 00:00:00"), 120);

        Price newPrice1 = new Price("1", 1, 1, simpleDateFormat.parse("10.01.2013 00:00:00"), simpleDateFormat.parse("20.01.2013 00:00:00"), 110);

        Price expectedPrice1 = new Price("1", 1, 1, simpleDateFormat.parse("01.01.2013 00:00:00"), simpleDateFormat.parse("10.01.2013 00:00:00"), 100);
        Price expectedPrice2 = new Price("1", 1, 1, simpleDateFormat.parse("10.01.2013 00:00:00"), simpleDateFormat.parse("20.01.2013 00:00:00"), 110);
        Price expectedPrice3 = new Price("1", 1, 1, simpleDateFormat.parse("20.01.2013 00:00:00"), simpleDateFormat.parse("30.01.2013 00:00:00"), 120);

        Collection<Price> joinedPrices = priceService.joinPrices(Arrays.asList(existedPrice1, existedPrice2), Arrays.asList(newPrice1));

        assertThat(joinedPrices.size(), is(3));
        assertTrue(joinedPrices.containsAll(Arrays.asList(expectedPrice1, expectedPrice2, expectedPrice3)));
    }

    @Test
    public void testJoinPricesWithOverlapExistedPrice() throws Exception {
        Price existedPrice1 = new Price("1", 1, 1, simpleDateFormat.parse("01.01.2013 00:00:00"), simpleDateFormat.parse("10.01.2013 00:00:00"), 80);
        Price existedPrice2 = new Price("1", 1, 1, simpleDateFormat.parse("10.01.2013 00:00:00"), simpleDateFormat.parse("20.01.2013 00:00:00"), 87);
        Price existedPrice3 = new Price("1", 1, 1, simpleDateFormat.parse("20.01.2013 00:00:00"), simpleDateFormat.parse("30.01.2013 00:00:00"), 90);

        Price newPrice1 = new Price("1", 1, 1, simpleDateFormat.parse("05.01.2013 00:00:00"), simpleDateFormat.parse("15.01.2013 00:00:00"), 80);
        Price newPrice2 = new Price("1", 1, 1, simpleDateFormat.parse("15.01.2013 00:00:00"), simpleDateFormat.parse("25.01.2013 00:00:00"), 85);

        Price expectedPrice1 = new Price("1", 1, 1, simpleDateFormat.parse("01.01.2013 00:00:00"), simpleDateFormat.parse("15.01.2013 00:00:00"), 80);
        Price expectedPrice2 = new Price("1", 1, 1, simpleDateFormat.parse("15.01.2013 00:00:00"), simpleDateFormat.parse("25.01.2013 00:00:00"), 85);
        Price expectedPrice3 = new Price("1", 1, 1, simpleDateFormat.parse("25.01.2013 00:00:00"), simpleDateFormat.parse("30.01.2013 00:00:00"), 90);

        Collection<Price> joinedPrices = priceService.joinPrices(Arrays.asList(existedPrice1, existedPrice2, existedPrice3), Arrays.asList(newPrice1, newPrice2));

        assertThat(joinedPrices.size(), is(3));
        assertTrue(joinedPrices.containsAll(Arrays.asList(expectedPrice1, expectedPrice2, expectedPrice3)));
    }
}