#load "../AocUtil.csx"

using System;
using System.Collections.Immutable;

class Day21_AllergenAssessment : AocUtil.Solver {

    public long Solve(ImmutableList<string> lines, int part) {
        var foodIngredients = new List<HashSet<string>>();
        var foodAllergens = new List<HashSet<string>>();
        foreach (string line in lines) {
            string[] parts = line.Split(" (contains ");

            var ingredients = new HashSet<string>();
            foreach (string ingredient in parts[0].Split(" ")) {
                ingredients.Add(ingredient);
            }
            foodIngredients.Add(ingredients);

            var allergens = new HashSet<string>();
            string sAllergen = parts[1].Substring(0, parts[1].Length - 1);
            foreach (string allergen in sAllergen.Split(", ")) {
                allergens.Add(allergen);
            }
            foodAllergens.Add(allergens);
        }

        // Note each possible ingredient for each allergen.
        var allergenToIngredients = new Dictionary<string, HashSet<string>>();
        for (int i = 0; i < foodIngredients.Count; i++) {
            HashSet<string> ingredients = foodIngredients[i];
            HashSet<string> allergens = foodAllergens[i];
            foreach (string allergen in allergens) {
                if (!allergenToIngredients.ContainsKey(allergen)) {
                    allergenToIngredients[allergen] = new HashSet<string>();
                    foreach (string ingredient in ingredients) {
                        allergenToIngredients[allergen].Add(ingredient);
                    }
                } else {
                    allergenToIngredients[allergen].IntersectWith(ingredients);
                }
            }
        }

        // Eliminate ingredients with a known allergen from other allergens' candidate sets.
        var allergensDeduced = new Queue<string>();
        foreach (string allergen in allergenToIngredients.Keys) {
            if (allergenToIngredients[allergen].Count == 1) {
                allergensDeduced.Enqueue(allergen);
            }
        }
        var allergenToIngredient = new Dictionary<string, string>();
        while (allergensDeduced.Count > 0) {
            string allergen = allergensDeduced.Dequeue();
            if (allergenToIngredient.ContainsKey(allergen)) {
                continue;
            }

            string ingredient = SomeValue(allergenToIngredients[allergen]);
            allergenToIngredient[allergen] = ingredient;

            foreach (string allergenOther in allergenToIngredients.Keys) {
                if (allergenOther == allergen || !allergenToIngredients[allergenOther].Contains(ingredient)) {
                    continue;
                }
                allergenToIngredients[allergenOther].Remove(ingredient);
                if (allergenToIngredients[allergenOther].Count == 1) {
                    allergensDeduced.Enqueue(allergenOther);
                }
            }
        }

        if (part == 1) {
            var ingredientCandidates = new HashSet<string>();
            foreach (string allergen in allergenToIngredients.Keys) {
                foreach (string ingredient in allergenToIngredients[allergen]) {
                    ingredientCandidates.Add(ingredient);
                }
            }

            long answer = 0L;
            foreach (HashSet<string> ingredients in foodIngredients) {
                foreach (string ingredient in ingredients) {
                    if (!ingredientCandidates.Contains(ingredient)) {
                        answer++;
                    }
                }
            }
            return answer;
        } else {
            var allergens = new List<string>();
            foreach (string allergen in allergenToIngredient.Keys) {
                allergens.Add(allergen);
            }
            allergens.Sort();
            var ingredients = new List<string>();
            foreach (string allergen in allergens) {
                ingredients.Add(allergenToIngredient[allergen]);
            }
            Console.WriteLine(String.Join(",", ingredients));
        }

        return -1;
    }

    private static string SomeValue(HashSet<string> set) {
        foreach (string value in set) {
            return value;
        }
        throw new SystemException("Empty set.");
    }
}

AocUtil.RunSolver(21, Args, new Day21_AllergenAssessment());
