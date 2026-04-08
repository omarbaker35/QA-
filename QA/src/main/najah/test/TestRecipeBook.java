package main.najah.test;

import static org.junit.jupiter.api.Assertions.*;

import java.time.Duration;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import main.najah.code.Recipe;
import main.najah.code.RecipeBook;
import main.najah.code.RecipeException;

@DisplayName("RecipeBook Tests")
@Execution(ExecutionMode.CONCURRENT)
public class TestRecipeBook {

    private RecipeBook recipeBook;

    @BeforeEach
    void setUp() {
        recipeBook = new RecipeBook();
    }

    private Recipe buildRecipe(String name, String price, String coffee, String milk, String sugar, String chocolate)
            throws RecipeException {
        Recipe recipe = new Recipe();
        recipe.setName(name);
        recipe.setPrice(price);
        recipe.setAmtCoffee(coffee);
        recipe.setAmtMilk(milk);
        recipe.setAmtSugar(sugar);
        recipe.setAmtChocolate(chocolate);
        return recipe;
    }

    @Test
    @DisplayName("getRecipes should return array of size 4 with empty slots initially")
    void testGetRecipesInitially() {
        Recipe[] recipes = recipeBook.getRecipes();

        assertAll(
            () -> assertEquals(4, recipes.length),
            () -> assertNull(recipes[0]),
            () -> assertNull(recipes[1]),
            () -> assertNull(recipes[2]),
            () -> assertNull(recipes[3])
        );
    }

    @ParameterizedTest(name = "recipe {0} should be added successfully")
    @ValueSource(strings = {"Espresso", "Latte", "Mocha"})
    @DisplayName("addRecipe should add recipe when slot is available")
    void testAddRecipe(String recipeName) throws RecipeException {
        Recipe recipe = buildRecipe(recipeName, "10", "1", "1", "1", "1");
        boolean added = recipeBook.addRecipe(recipe);

        assertAll(
            () -> assertTrue(added),
            () -> assertEquals(recipeName, recipeBook.getRecipes()[0].getName())
        );
    }

    @Test
    @DisplayName("addRecipe should reject duplicate recipes")
    void testAddDuplicateRecipe() throws RecipeException {
        Recipe first = buildRecipe("Espresso", "10", "1", "1", "1", "1");
        Recipe duplicate = buildRecipe("Espresso", "20", "2", "2", "2", "2");

        assertAll(
            () -> assertTrue(recipeBook.addRecipe(first)),
            () -> assertFalse(recipeBook.addRecipe(duplicate))
        );
    }

    @Test
    @DisplayName("addRecipe should return false when recipe book is full")
    void testAddRecipeWhenFull() throws RecipeException {
        assertTrue(recipeBook.addRecipe(buildRecipe("R1", "10", "1", "1", "1", "1")));
        assertTrue(recipeBook.addRecipe(buildRecipe("R2", "10", "1", "1", "1", "1")));
        assertTrue(recipeBook.addRecipe(buildRecipe("R3", "10", "1", "1", "1", "1")));
        assertTrue(recipeBook.addRecipe(buildRecipe("R4", "10", "1", "1", "1", "1")));

        assertFalse(recipeBook.addRecipe(buildRecipe("R5", "10", "1", "1", "1", "1")));
    }

    @Test
    @DisplayName("deleteRecipe should return recipe name and reset its slot")
    void testDeleteRecipeExisting() throws RecipeException {
        Recipe recipe = buildRecipe("Latte", "15", "1", "1", "1", "1");
        recipeBook.addRecipe(recipe);

        String deletedName = recipeBook.deleteRecipe(0);

        assertAll(
            () -> assertEquals("Latte", deletedName),
            () -> assertNotNull(recipeBook.getRecipes()[0]),
            () -> assertEquals("", recipeBook.getRecipes()[0].getName())
        );
    }

    @Test
    @DisplayName("deleteRecipe should return null for empty slot")
    void testDeleteRecipeMissing() {
        assertNull(recipeBook.deleteRecipe(0));
    }

    @Test
    @DisplayName("editRecipe should return old name and replace recipe")
    void testEditRecipeExisting() throws RecipeException {
        Recipe oldRecipe = buildRecipe("OldCoffee", "10", "1", "1", "1", "1");
        Recipe newRecipe = buildRecipe("NewCoffee", "20", "2", "2", "2", "2");

        recipeBook.addRecipe(oldRecipe);
        String oldName = recipeBook.editRecipe(0, newRecipe);

        assertAll(
            () -> assertEquals("OldCoffee", oldName),
            () -> assertNotNull(recipeBook.getRecipes()[0]),
            () -> assertEquals("", recipeBook.getRecipes()[0].getName()),
            () -> assertEquals(20, recipeBook.getRecipes()[0].getPrice())
        );
    }

    @Test
    @DisplayName("editRecipe should return null when slot is empty")
    void testEditRecipeMissing() throws RecipeException {
        Recipe newRecipe = buildRecipe("NewCoffee", "20", "2", "2", "2", "2");
        assertNull(recipeBook.editRecipe(0, newRecipe));
    }

    @Test
    @DisplayName("recipe book operations should finish quickly")
    void testTimeout() {
        assertTimeout(Duration.ofMillis(100), () -> {
            recipeBook.getRecipes();
        });
    }
}