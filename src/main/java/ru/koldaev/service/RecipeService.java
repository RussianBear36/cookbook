package ru.koldaev.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.thymeleaf.util.StringUtils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Service
public class RecipeService {

    private Connection connection;

    @Autowired
    public void setConnection(Connection connection) {
        this.connection = connection;
    }

    public int getAuthorId(String login) throws SQLException {
        int id = -1;
        PreparedStatement ps = connection.prepareStatement("select id from usr where login = ?");
        ps.setString(1, login);
        ResultSet rs = ps.executeQuery();
        if (rs.next()) {
            id = rs.getInt("id");
        }
        return id;
    }

    public int getBaseId(String base) throws SQLException {
        int id = -1;
        PreparedStatement ps = connection.prepareStatement("select id from base where name = ?");
        ps.setString(1, base);
        ResultSet rs = ps.executeQuery();
        if (rs.next()) {
            id = rs.getInt("id");
        }
        return id;
    }

    public int getKitchenId(String kitchen) throws SQLException {
        int id = -1;
        PreparedStatement ps = connection.prepareStatement("select id from kitchen where name = ?");
        ps.setString(1, kitchen);
        ResultSet rs = ps.executeQuery();
        if (rs.next()) {
            id = rs.getInt("id");
        }
        return id;
    }

    public int getCategoryId(String category) throws SQLException {
        int id = -1;
        PreparedStatement ps = connection.prepareStatement("select id from category where name = ?");
        ps.setString(1, category);
        ResultSet rs = ps.executeQuery();
        if (rs.next()) {
            id = rs.getInt("id");
        }
        return id;
    }

    public int getCookMethodId(String cookMethod) throws SQLException {
        int id = -1;
        PreparedStatement ps = connection.prepareStatement("select id from cooking_method where name = ?");
        ps.setString(1, cookMethod);
        ResultSet rs = ps.executeQuery();
        if (rs.next()) {
            id = rs.getInt("id");
        }
        return id;
    }

    public int getIngredientId(String ingredient) throws SQLException {
        int id = -1;
        PreparedStatement ps = connection.prepareStatement("select id from ingredient where name = ?");
        ps.setString(1, ingredient);
        ResultSet rs = ps.executeQuery();
        if (rs.next()) {
            id = rs.getInt("id");
        }
        return id;
    }


    public int getUnitId(String unit) throws SQLException {
        int id = -1;
        PreparedStatement ps = connection.prepareStatement("select id from unit where name = ?");
        ps.setString(1, unit);
        ResultSet rs = ps.executeQuery();
        if (rs.next()) {
            id = rs.getInt("id");
        }
        return id;
    }

    public boolean createRecipe
            (
                    String recipeName,
                    String theBase,
                    String theKitchen,
                    String theCategory,
                    String[] theCookMethod,
                    String[] ingredientName,
                    String[] countName,
                    String[] unitName,
                    String description,
                    String calories,
                    String proteins,
                    String fats,
                    String carbohydrates,
                    String portions,
                    String source,
                    String authorLogin
            ) throws SQLException {

        //Получаем id для рецепта
        int nextRecipeId = -1;
        PreparedStatement nextIdString = connection.prepareStatement("SELECT nextval('recipe_id_seq');");
        ResultSet resultSetId = nextIdString.executeQuery();
        if (resultSetId.next()) {
            nextRecipeId = resultSetId.getInt("nextval");
        }
        //Получаем id автора
        int authorId = getAuthorId(authorLogin);
        //Получаем id категории
        int categoryId = getCategoryId(theCategory);

        //Вставляем источник
        int nextSourceId = -1;
        PreparedStatement nextSourceIdString = connection.prepareStatement("SELECT nextval('source_id_seq');");
        ResultSet resultSetSourceId = nextSourceIdString.executeQuery();
        if (resultSetSourceId.next()) {
            nextSourceId = resultSetSourceId.getInt("nextval");
        }
        PreparedStatement sourcePs = connection.prepareStatement("insert into source (id, name) values (?, ?)");
        sourcePs.setInt(1, nextSourceId);
        sourcePs.setString(2, source);
        sourcePs.execute();

        //Определяем время создания рецепта
        LocalDateTime now = LocalDateTime.now(ZoneId.of("Europe/Moscow"));
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy hh.mm");

        //Вставляем первые значения в таблицу рецепта
        String insertRecipe = "insert into recipe " +
                "(id, description, category_id, source_id, name, author_id, date_added)" +
                "values (?, ?, ?, ?, ?, ?, ?)";
        PreparedStatement ps = connection.prepareStatement(insertRecipe);
        ps.setInt(1, nextRecipeId);
        ps.setString(2, description);
        ps.setInt(3, categoryId);
        ps.setInt(4, nextSourceId);
        ps.setString(5, recipeName);
        ps.setInt(6, authorId);
        ps.setString(7, now.format(formatter));
        //ps.setInt(8, Integer.parseInt(portions));
        ps.execute();

        //Получаем id способов приготовления
        List<Integer> cookMethodId = new ArrayList<>();
        if (!(theCookMethod.length == 1 && theCookMethod[0].equals("Не выбрано"))) {
            for (String s : theCookMethod) {
                cookMethodId.add(getCookMethodId(s));
            }
        }
        //Вставляем в таблицу "способы приготовления рецепта значения"
        for (Integer i : cookMethodId) {
            PreparedStatement cookMethodPs = connection.prepareStatement("insert into method_cooking_recipe (recipe_id, cooking_method_id) values (?, ?)");
            cookMethodPs.setInt(1, nextRecipeId);
            cookMethodPs.setInt(2, i);
            cookMethodPs.execute();
        }

        //Вставляем ингредиенты и получем все их id
        List<Integer> ingredientId = new ArrayList<>();
        for (String s : ingredientName) {
            int nextIngredientId = -1;
            PreparedStatement nextIngredientIdString = connection.prepareStatement("SELECT nextval('ingredient_id_seq');");
            ResultSet resultSetIngredientId = nextIngredientIdString.executeQuery();
            if (resultSetIngredientId.next()) {
                nextIngredientId = resultSetIngredientId.getInt("nextval");
            }
            PreparedStatement insertIngredient = connection.prepareStatement("insert into ingredient (id, name) values (?, ?)");
            insertIngredient.setInt(1, nextIngredientId);
            insertIngredient.setString(2, s);
            insertIngredient.execute();
            ingredientId.add(nextIngredientId);
        }

        //Получаем id мер измерения
        List<Integer> unitId = new ArrayList<>();
        for (int i = 0; i < unitName.length; i++) {
            unitId.add(getUnitId(unitName[i]));
        }

        //Заполняем таблицу "состав рецепта"
        int k = 0;
        for (Integer i : ingredientId) {
            PreparedStatement compositionPs = connection.prepareStatement("insert into recipe_composition (recipe_id, ingredient_id, count, unit_id) values (?, ?, ?, ?)");
            compositionPs.setInt(1, nextRecipeId);
            compositionPs.setInt(2, i);
            compositionPs.setString(3, countName[k]);
            compositionPs.setInt(4, unitId.get(k));
            compositionPs.execute();
            k++;
        }

        //Находим, если надо, id основы, кухни и добавляем к строке рецепта
        int baseId = -1;
        int kitchenId = -1;
        if (!theBase.equals("Не выбрано")) {
            baseId = getBaseId(theBase);
            PreparedStatement updateBase = connection.prepareStatement("update recipe set base_id = ? where id = ?");
            updateBase.setInt(1, baseId);
            updateBase.setInt(2, nextRecipeId);
            updateBase.execute();
        }
        if (!theKitchen.equals("Не выбрано")) {
            kitchenId = getKitchenId(theKitchen);
            PreparedStatement updateKitchen = connection.prepareStatement("update recipe set kitchen_id = ? where id = ?");
            updateKitchen.setInt(1, kitchenId);
            updateKitchen.setInt(2, nextRecipeId);
            updateKitchen.execute();
        }

        //Если указаны калории - вставляем
        if (!StringUtils.isEmpty(calories)) {
            PreparedStatement update = connection.prepareStatement("update recipe set calorie_content = ? where id = ?");
            update.setInt(1, Integer.parseInt(calories));
            update.setInt(2, nextRecipeId);
            update.execute();
        }
        //Если указаны Белки - вставляем
        if (!StringUtils.isEmpty(proteins)) {
            PreparedStatement update = connection.prepareStatement("update recipe set protein_content = ? where id = ?");
            update.setInt(1, Integer.parseInt(proteins));
            update.setInt(2, nextRecipeId);
            update.execute();
        }
        //Если указаны жиры  - вставляем
        if (!StringUtils.isEmpty(fats)) {
            PreparedStatement update = connection.prepareStatement("update recipe set fat_content = ? where id = ?");
            update.setInt(1, Integer.parseInt(fats));
            update.setInt(2, nextRecipeId);
            update.execute();
        }
        //Если указаны углеводы  - вставляем
        if (!StringUtils.isEmpty(carbohydrates)) {
            PreparedStatement update = connection.prepareStatement("update recipe set carbohydrate_content = ? where id = ?");
            update.setInt(1, Integer.parseInt(carbohydrates));
            update.setInt(2, nextRecipeId);
            update.execute();
        }
        //Если указаны порции  - вставляем
        if (!StringUtils.isEmpty(portions)) {
            PreparedStatement update = connection.prepareStatement("update recipe set portion = ? where id = ?");
            update.setInt(1, Integer.parseInt(portions));
            update.setInt(2, nextRecipeId);
            update.execute();
        }
        return true;
    }

    public void addToFavorite(String userLogin, int recipeId) throws SQLException
    {
        int userId = getAuthorId(userLogin);
        PreparedStatement ps = connection.prepareStatement("insert into favorites (user_id, recipe_id) VALUES (?, ?)");
        ps.setInt(1, userId);
        ps.setInt(2, recipeId);
        ps.execute();
    }

    public void deleteFromFavorite(String userLogin, int recipeId) throws SQLException
    {
        int userId = getAuthorId(userLogin);
        PreparedStatement ps = connection.prepareStatement("delete from favorites where user_id = ? and recipe_id = ?");
        ps.setInt(1, userId);
        ps.setInt(2, recipeId);
        ps.execute();
    }

    public void addReview(String user_login, int recipeId, String review) throws SQLException
    {
        int nextReviewId = -1;
        PreparedStatement ps1 = connection.prepareStatement("select nextval('review_id_seq')");
        ResultSet rs1 = ps1.executeQuery();
        while (rs1.next())
        {
            nextReviewId = rs1.getInt(1);
        }

        int userId = getAuthorId(user_login);
        PreparedStatement ps = connection.prepareStatement("insert into review (id, recipe_id, user_id, content, date_added) VALUES (?, ?, ?, ?, ?)");
        ps.setInt(1, nextReviewId);
        ps.setInt(2, recipeId);
        ps.setInt(3, userId);
        ps.setString(4, review);

        LocalDateTime localDateTime = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy hh:mm");
        String date = localDateTime.format(formatter);

        ps.setString(5, date);

        ps.execute();
    }


}
