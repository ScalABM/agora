package javaMarkets;

import markets.tradables.Tradable;

import java.util.Random;
import java.util.UUID;
import java.util.ArrayList;
import java.util.PriorityQueue;
import org.apache.commons.math3.distribution.GeometricDistribution;
import org.apache.commons.math3.random.MersenneTwister;

/**
 * This class implements two market mechanisms for a housing market:
 * 1 - modifiedClearMarket is the mechanism currently used by the Housing ABM
 * 2 - deferredAcceptanceClearMarket is the standard DA algorithm
 *
 * In both cases, houses have a quality parameter used to rank them. Offers have a desired price,
 * and bids have a maximum expenditure.
 *
 * To stick with the convention in the housing ABM, I'm using the words "offer" and "bid"
 *
 * I haven't extended any of the classes in the library, but have structured the code
 * so that can be done, hopefully with the least amount of 'surgery'
 * @author rafa
 */
public class HousingMarket {

    int MAX_NUMBER_OF_ROUNDS = 500;
    OfferBook offers;
    BidBook bids;
    final double BIDUP = 1.0075;
    public static final double UNDEROFFER = 7.0/30.0; // time (in months) that a house remains 'under offer'

    MersenneTwister rand = new MersenneTwister();



    /**
     * This is the tradable (a house) which includes a quality parameter.
     */
    class House implements Tradable {
        public double quality;
        private UUID uuid;

        @Override
        public UUID uuid() {
            return uuid;
        }

        public House(double quality) {

            this.quality=quality;
            uuid = UUID.randomUUID();
        }
    }

    /**
     * This is the ask order (i.e. an offer from a seller), which includes a house and an ask price.
     * This would extend the correct AskOrder (probably not a limit price since sellers are flexible with their price)
     * Maybe MarketAskOrder?
     *
     * Using Comparable as a temporary fix
     */
    class Offer implements Comparable<Offer> { // extends AskOrder
        public House house;
        public double desiredPrice;
        public double bidUpPrice;
        public double salePrice;

        // IMPORTANT. We must store a list of interested bids (those prematurely matched to this offer).
        // At the moment just an array list.
        private ArrayList<Bid> interestedBids;
        public Bid winnerBid;

        // The compare method would be replaced by an Ordering when using AskOrder
        @Override
        public int compareTo(Offer o) {
            return Double.compare(this.house.quality,o.house.quality); // Rank in terms of quality
            // TODO: CHECK THE SIGN
        }

        public Offer(House house, double desiredPrice) {
            this.house=house;  // Java is so ugly...
            this.desiredPrice=desiredPrice;
            this.interestedBids = new ArrayList<>();
        }

        public int numberOfMatchedBids() {
            return interestedBids.size();
        }
        public void matchWithBid(Bid bid) {
            interestedBids.add(bid);
        }

        public void clearMatchedBids() {
            interestedBids.clear();
        }

        public Bid pollBestInterestedBid() {
            if (interestedBids.isEmpty()) return null;

            // IMPORTANT: What's the best way to pick the best interested bid? Let's simply return the head of the list.
            // The seller is not supposed to know how much buyers are willing to pay so they should not rank buyers
            // by their desired expenditure.
            Bid winner = interestedBids.get(0);
            interestedBids.remove(0);
            salePrice = desiredPrice;
            return winner;
        }

        public void clearInterestedBids() {
            for (Bid bid : interestedBids) {
                bid.matchedOffer=null;
            }
            interestedBids.clear();
        }

        public void bidUpPrice() {
            int enoughBids = Math.min(4, (int)(0.5 +numberOfMatchedBids()));
            double pSuccessfulBid = Math.exp(-enoughBids*UNDEROFFER);
            GeometricDistribution geomDist = new GeometricDistribution(rand, pSuccessfulBid);
            bidUpPrice = desiredPrice * Math.pow(BIDUP, geomDist.sample());
        }

        /**
         * After we bid up the price, we pick the winner like this: we pick a bid at random that can still afford
         * the house at its higher price. If no one can, we pick one at random at the old price.
         *
         * @return the picked winner
         */
        public Bid pickWinnerAfterBidUp() {
            for (Bid bid: interestedBids) {
                if (bid.maxPrice>=bidUpPrice) {
                    interestedBids.remove(bid);
                    salePrice = bidUpPrice;
                    return bid;
                }
            }

            return pollBestInterestedBid();
        }


    }


    /**
     * This is the bid order (from the buyer). It includes the maximum price they are willing to afford,
     * so it should probably extend LimitBidOrder
     */
    class Bid { // extends LimitBidOrder
        public double maxPrice;
        public Offer matchedOffer; // each bid has only one matched offer at most

        public Bid(double maxPrice) {
            this.maxPrice=maxPrice;
        }

        public Offer findBestMatch(OfferBook offers) {
            // We choose our favourite offer: the offer with the
            // highest quality and with an asking price lower or equal than our desired price
            double bestQuality = 0;
            Offer bestOffer = null;

            // At the moment I'm iterating through the OfferBook. Maybe this should be done using a preference?
            for (Offer offer : offers) {
                if (offer.desiredPrice<=maxPrice && offer.house.quality > bestQuality) {
                    bestQuality = offer.house.quality;
                    bestOffer = offer;
                }
            }
            return bestOffer;
        }

    }


    /**
     * This is the ask orderbook with all the offers (at the moment just a priority queue).
     */
    class OfferBook extends PriorityQueue<Offer> { // extends OrderBook?

    }

    /**
     * This is the bid orderbook with all the bids (at the moment just a priority queue)
     */
    class BidBook extends PriorityQueue<Bid> { // extends OrderBook?
    }



    public void initialise() {
        offers = new OfferBook();
        bids = new BidBook();
    }

    /**
     * Deferred Acceptance. The standard algorithm includes the following modifications:
     *
     *  - the size of the offers set and the bids set is not equal. In the standard DA, they are.
     *
     *  - the preferences of bids do not cover all the offers (for example, a bid will never prefer an
     *    offer that's asking for more money than they are willing to spend). In the standard DA, all men
     *    must have an ordering over all women and vice versa.
     *
     */
    public void deferredAcceptanceClearMarket() {
        int round=0;
        while(round<MAX_NUMBER_OF_ROUNDS) {
            /**
             * First half of the loop: iterate through the bids not yet matched,
             * and match each bid to one offer (or possibly no offers)
             */
            for (Bid bid : bids) {
                if (bid.matchedOffer == null) {
                    Offer offer = bid.findBestMatch(offers);
                    if (offer != null) offer.matchWithBid(bid);
                }
            }

            /**
             * Second half of the loop. Iterate through the offers: for each offer, match it with its best matched bid,
             * and skip if it has no matched bids.
             *
             * IMPORTANT: the seller needs to choose its preferred bid out of the list of interested bids.
             */
            for (Offer offer : offers) {
                if (offer.numberOfMatchedBids() == 0) ; // do nothing?
                else {
                    offer.winnerBid = offer.pollBestInterestedBid();
                    offer.clearInterestedBids();
                }
            }

        }
    }


    /**
     * This is the current algorithm used in our housing ABM. It differs from the Standard DA in that:
     *
     * - offers with more than one interested bid get to bid up and filter out those interested bids which
     *   can no longer afford the house
     *
     * - as soon as an offer is matched with a bid, the offer settles and cannot be matched to anything else
     */
    public void modifiedClearMarket() {

        /**
         * First half of the loop: iterate through the bids not yet matched,
         * and match each bid to one offer (or possibly no offers)
         *
         * This is the same as in DA
         */
        for (Bid bid : bids) {
            if (bid.matchedOffer == null) {
                Offer offer = bid.findBestMatch(offers);
                if (offer != null) offer.matchWithBid(bid);
            }
        }

        /**
         * Second half of the loop. Iterate through the offers: for those offers with no matched bids, skip.
         * For those with some matched bids, bid up price, filter out bids that can no longer afford the price,
         * and pick one at random.
         */

        for (Offer offer : offers) {

            // Important: we only alter those offers who have not picked a winner yet; whereas in standard DA,
            // all offers should be able to update their best pick so far.
            if (offer.winnerBid == null) {

                if (offer.numberOfMatchedBids() > 0) {
                    // if we have interested bids, bid up the price
                    offer.bidUpPrice();

                    // pick a bid at random which can afford the new price; if no one can, pick the first one
                    // at the old price
                    offer.winnerBid = offer.pickWinnerAfterBidUp();
                    offer.clearInterestedBids();
                }
            }
        }

    }


}

