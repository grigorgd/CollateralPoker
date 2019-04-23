package game;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

import java.io.File;

/**
 * Object <code>CardImage</code> represents a image of card, both front and back
 */
public class CardImage extends StackPane {
    private static final String CARD_PATH = "/cards/%s.png";
    private static final String[] back = {"rectangle_blue", "rectangle_red", "rectangle_yellow", "rectangle_green"};
    private static int backIndex = 0;
    private static boolean fourColour = false;
    private String cardString = "";

    public CardImage(String fileName){
        this.cardString = fileName;
        create();
        this.setLayoutX(60);
    }

    public CardImage(double displacement){
        cardString = back[backIndex];
        create();
        this.setLayoutX(this.getLayoutX() + displacement);
    }

    public CardImage(Card card, double displacement, boolean fourColour){
        this.fourColour = fourColour;
        cardString = card.toString();
        if(fourColour && (card.getSuit().equals(Suit.CLUBS) || card.getSuit().equals(Suit.DIAMONDS))){
            cardString += "_x";
        }
        create();
        this.setLayoutX(this.getLayoutX() + displacement);
    }

    private void create(){
        this.getChildren().addAll(createRec(), setImage());
    }

    private Rectangle createRec(){
        Rectangle rec = new Rectangle(72, 108);
        rec.setArcHeight(10);
        rec.setArcWidth(10);
        rec.setStroke(Color.GRAY);
        rec.setFill(Color.WHITE);
        return rec;
    }

    private ImageView setImage(){
        ImageView image = new ImageView();
        image.setPreserveRatio(true);
        image.setImage(new Image(this.getClass().getResourceAsStream(String.format(CARD_PATH, cardString))));
        image.setFitHeight(102);
        return image;
    }

    public static void setBack(){ backIndex = (backIndex + 1) % back.length; }
}
