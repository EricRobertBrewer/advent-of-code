#include "aoc.h"

#define INPUT_DIR "../input"
#define ENV_VAR "AOC_SESSION"

char *aoc_download_input_if_needed(const char *year, int day) {
    // Create year directory.
    printf("Making year directory: `%s`\n", year);
    char *year_dir_name = malloc((strlen(INPUT_DIR) + strlen(year) + 2) * sizeof(char));
    sprintf(year_dir_name, "%s/%s", INPUT_DIR, year);
    if (mkdir(year_dir_name, 0777) != 0 && errno != EEXIST) {
        fprintf(stderr, "Unable to create year directory: %s", strerror(errno));
    }

    // Check if file exists.
    char *day_path = malloc((strlen(year_dir_name) + 8) * sizeof(char));
    sprintf(day_path, "%s/%02d.txt", year_dir_name, day);
    free(year_dir_name);
    printf("Accessing file `%s`...", day_path);
    if (access(day_path, F_OK) != 0) {
        // Download input file.
        // https://curl.se/libcurl/c/https.html
        // https://stackoverflow.com/a/21573625/1559071
        curl_global_init(CURL_GLOBAL_DEFAULT);
        CURL *curl;
        curl = curl_easy_init();
        if (curl) {
            char *cookie_header = NULL;
            struct curl_slist *chunk = NULL; // For storing header.
            char *url = NULL;
            FILE *day_file;
            CURLcode res;

            // Read environment variable `AOC_SESSION` and add header.
            const char *token = getenv(ENV_VAR); // For user session.
            if (token == NULL) {
                fprintf(stderr, "Set the environment variable `%s` as your AoC session token. "
                                "(Found in Dev Tools > Application > Cookies > 'session')\n"
                                "export %s=<my_token>\n", ENV_VAR, ENV_VAR);
                exit(1);
            } else {
                // https://curl.se/libcurl/c/httpcustomheader.html
                cookie_header = malloc((strlen(token) + 17) * sizeof(char));
                sprintf(cookie_header, "cookie: session=%s", token);
                chunk = curl_slist_append(chunk, cookie_header);
            }
            curl_easy_setopt(curl, CURLOPT_HTTPHEADER, chunk);

            // Specify web address.
            url = malloc((strlen(year) + (day > 9 ? 2 : 1) + 37) * sizeof(char));
            sprintf(url, "https://adventofcode.com/%s/day/%d/input", year, day);
            curl_easy_setopt(curl, CURLOPT_URL, url);

            // Specify file location.
            day_file = fopen(day_path, "wb");
            curl_easy_setopt(curl, CURLOPT_WRITEFUNCTION, NULL);
            curl_easy_setopt(curl, CURLOPT_WRITEDATA, day_file);

            res = curl_easy_perform(curl);
            if (res != CURLE_OK) {
                fprintf(stderr, "`curl_easy_perform()` failed: %s\n", curl_easy_strerror(res));
            }

            fclose(day_file);
            free(url);
            curl_slist_free_all(chunk);
            free(cookie_header);
            curl_easy_cleanup(curl);
        }
        curl_global_cleanup();
    }
    return day_path;
}

char *aoc_example_path(const char *year, int day) {
    char *example_path = malloc((strlen(INPUT_DIR) + strlen(year) + 17) * sizeof(char));
    sprintf(example_path, "%s/%s/%02d-example.txt", INPUT_DIR, year, day);
    return example_path;
}
