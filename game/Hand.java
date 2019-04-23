package game;

/**
 * Enum <code>Hand</code>represents abbreviated names of all possible hands in game
 *      hand         configuration
 *      HighCard        6
 *      Pair            6
 *      TwoPairs        15
 *      ThreeOfAKind    6
 *      Straight        2
 *      Flush           4
 *      Full house      30
 *      FourOfAKind     6
 *      StraightFlush   4
 *      RoyalFlush      4
 *      Total           83
 *
 */
public enum Hand {
    high_card_9(0), high_card_T(1), high_card_J(2), high_card_Q(3), high_card_K(4), high_card_A(5),
    pair_9(6), pair_T(7), pair_J(8), pair_Q(9), pair_K(10), pair_A(11),
    two_pairs_T9(12), two_pairs_J9(13), two_pairs_JT(14), two_pairs_Q9(15), two_pairs_QT(16),
    two_pairs_QJ(17), two_pairs_K9(18), two_pairs_KT(19), two_pairs_KJ(20), two_pairs_KQ(21),
    two_pairs_A9(22), two_pairs_AT(23), two_pairs_AJ(24), two_pairs_AQ(25), two_pairs_AK(26),
    three_of_a_kind_9(27), three_of_a_kind_T(28), three_of_a_kind_J(29), three_of_a_kind_Q(30), three_of_a_kind_K(31), three_of_a_kind_A(32),
    small_straight(33), big_straight(34),
    flush_diamonds(35), flush_hearts(35), flush_spade(35), flush_clubs(35),
    full_house_9T(36), full_house_9J(37), full_house_9Q(38), full_house_9K(39), full_house_9A(40),
    full_house_T9(41), full_house_TJ(42), full_house_TQ(43), full_house_TK(44), full_house_TA(45),
    full_house_J9(46), full_house_JT(47), full_house_JQ(48), full_house_JK(49), full_house_JA(50),
    full_house_Q9(51), full_house_QT(52), full_house_QJ(53), full_house_QK(54), full_house_QA(55),
    full_house_K9(56), full_house_KT(57), full_house_KJ(58), full_house_KQ(59), full_house_KA(60),
    full_house_A9(61), full_house_AT(62), full_house_AJ(63), full_house_AQ(64), full_house_AK(65),
    four_of_a_kind_9(66), four_of_a_kind_T(67), four_of_a_kind_J(68), four_of_a_kind_Q(69), four_of_a_kind_K(70), four_of_a_kind_A(71),
    straight_flush_diamonds(72), straight_flush_hearts(72), straight_flush_spades(72),straight_flush_clubs(72),
    royal_flush_diamonds(73), royal_flush_hearts(73), royal_flush_spades(73), royal_flush_clubs(73);

    private int value;

    Hand(int value){
        this.value = value;
    }

    /**
     * @return hand value which is strength of hand in game;
     */
    public int getValue(){
        return this.value;
    }


}