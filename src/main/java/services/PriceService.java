package services;

import domain.Price;

import java.util.*;

import static services.PriceService.TimePeriodRelation.*;

public class PriceService {

    public Collection<Price> joinPrices(Collection<Price> existedPrices, Collection<Price> newPrices) {
        if (existedPrices.isEmpty()) {
            return newPrices;
        }

        for (Price newPrice : newPrices) {
            existedPrices = joinPrice(existedPrices, newPrice);
        }

        return existedPrices;
    }

    public Collection<Price> joinPrice(Collection<Price> existedPrices, Price newPrice) {
        ArrayList<Price> result = new ArrayList<>();

        Price copyNewPrice = copyPrice(newPrice);

        for (Price existedPrice : existedPrices) {
            if (isPricesEquals(copyNewPrice, existedPrice)) {
                if (copyNewPrice.getValue() == existedPrice.getValue()) {
                    copyNewPrice = addPricesPeriods(copyNewPrice, existedPrice);
                } else {
                    result.addAll(getPricesPeriodDifference(existedPrice, copyNewPrice));
                }
            } else {
                result.add(existedPrice);
            }
        }
        result.add(copyNewPrice);

        return result;
    }

    public boolean isPricesEquals(Price p1, Price p2) {
        return p1.getProductCode().equals(p2.getProductCode())
                && p1.getNumber() == p2.getNumber()
                && p1.getDepart() == p2.getDepart();
    }

    public List<Price> getPricesPeriodDifference(Price existedPrice, Price newPrice) {
        ArrayList<Price> result = new ArrayList<>();

        if (!isPricesPeriodsIntersected(existedPrice, newPrice)) {
            Collections.addAll(result, existedPrice);
        } else {
            switch (getPricesTimePeriodRelations(existedPrice, newPrice)) {
                case FORWARD_OFFSET:
                    existedPrice.setEnd(new Date(newPrice.getBegin().getTime()));
                    Collections.addAll(result, existedPrice);
                    break;
                case BACKWARD_OFFSET:
                    existedPrice.setBegin(new Date(newPrice.getEnd().getTime()));
                    Collections.addAll(result, existedPrice);
                    break;
                case OCCURRENCE:
                    Price firstPrice = copyPrice(existedPrice);
                    firstPrice.setEnd(new Date(newPrice.getBegin().getTime()));
                    existedPrice.setBegin(new Date(newPrice.getEnd().getTime()));
                    Collections.addAll(result, firstPrice, existedPrice);
                    break;
                case ABSORPTION:
                    break;
                case MATCH:
                default:
                    break;
            }
        }

        return result;
    }


    private boolean isPricesPeriodsIntersected(Price existed, Price newPrice) {
        return !(existed.getEnd().before(newPrice.getBegin()) || existed.getEnd().equals(newPrice.getBegin())
                || existed.getBegin().after(newPrice.getEnd()) || existed.getBegin().equals(newPrice.getEnd()));
    }

    private Price addPricesPeriods(Price existedPrice, Price newPrice) {
        long newBeginDate = Math.min(existedPrice.getBegin().getTime(), newPrice.getBegin().getTime());
        long newEndDate = Math.max(existedPrice.getEnd().getTime(), newPrice.getEnd().getTime());

        existedPrice.setBegin(new Date(newBeginDate));
        existedPrice.setEnd(new Date(newEndDate));

        return existedPrice;
    }

    public TimePeriodRelation getPricesTimePeriodRelations(Price existedPrice, Price newPrice) {
        if (newPrice.getBegin().after(existedPrice.getBegin()) && newPrice.getBegin().before(existedPrice.getEnd()) && newPrice.getEnd().after(existedPrice.getEnd())) {
            return FORWARD_OFFSET;
        } else if (newPrice.getBegin().before(existedPrice.getBegin()) && newPrice.getEnd().before(existedPrice.getEnd())) {
            return BACKWARD_OFFSET;
        } else if (newPrice.getBegin().after(existedPrice.getBegin()) && newPrice.getEnd().before(existedPrice.getEnd())) {
            return OCCURRENCE;
        } else if (newPrice.getBegin().before(existedPrice.getBegin()) && newPrice.getEnd().after(existedPrice.getEnd())) {
            return ABSORPTION;
        } else {
            return MATCH;
        }
    }

    public Price copyPrice(Price source) {
        return new Price(source.getProductCode(),
                source.getNumber(),
                source.getDepart(),
                new Date(source.getBegin().getTime()),
                new Date(source.getEnd().getTime()),
                source.getValue());
    }

    /**
     * Represents relations between prices periods.
     * <p>
     * \\ - first period.
     * // - second period.
     *
     * @FORWARD_OFFSET -  / \ / \
     * @BACKWARD_OFFSET - \ / \ /
     * @OCCURRENCE - / \ \ /
     * @ABSORPTION - \ / / \
     * @MATCH - X X
     */
    enum TimePeriodRelation {
        FORWARD_OFFSET,
        BACKWARD_OFFSET,
        OCCURRENCE,
        ABSORPTION,
        MATCH
    }

}
