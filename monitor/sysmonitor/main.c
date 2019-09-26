#include <stdio.h>
#include <filesystem>

#include "third/json.hpp"
#include "third/file.hpp"
#include "app.hpp"
#include "sys-counters.hpp"


using Path = std::experimental::filesystem::path;
using json = nlohmann::json;


static const char* CONFIG_FILE_NAME = "config.json";
static const char* JSON_METRICS = "metrics";
static const char* JSON_METRICS_NAME = "name";
static const char* JSON_METRICS_COUNTER = "counter";
static const char* JSON_METRICS_KIND = "kind";
static const char* JSON_TOPIC = "topic";
static const char* JSON_PERIOD = "period";
static const char* JSON_KAFKA = "kafka";


int main(int argc, char **argv) {
    try {
        auto directory = Path(argv[0]).remove_filename();
        auto path = (directory / CONFIG_FILE_NAME).generic_string();
        std::experimental::filesystem::current_path(directory);

        std::string corpus;
        File(path.c_str(), "rb").load_file(corpus);
        auto config = json::parse(corpus);

        SysCounters counters;
        auto metrics = config[JSON_METRICS];
        for (const auto& m : metrics) {
            counters.addMetrics(m[JSON_METRICS_NAME].get<std::string>(),
                m[JSON_METRICS_COUNTER].get<std::string>(), m[JSON_METRICS_KIND].get<std::string>());
        }

        // uncomment next row for testing your own metrics only
        //counters.traceMetrics(10, 1);
        //return 0;

        auto topic = config[JSON_TOPIC].get<std::string>();
        auto period = config[JSON_PERIOD].get<int>();

        App app;
        app.create(config[JSON_KAFKA], topic.c_str());
        printf("Run. \ntopic: %s\nperiod: %d\n\nPress Ctrl-C to exit\n", topic.c_str(), period);
        config.clear();
        corpus.clear();

        app.run(counters, period);
        app.flush(10000);
        return 0;
    } catch (std::exception error) {
        printf("Internal application error: %s\n", error.what());
        return 1;
    }
}
