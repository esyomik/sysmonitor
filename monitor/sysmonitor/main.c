#include <stdio.h>
#include <filesystem>

#include "third/json.hpp"
#include "third/file.hpp"
#include "app.hpp"
#include "sys-counters.hpp"


using Path = std::experimental::filesystem::path;
using json = nlohmann::json;


static const char* CONFIG_FILE_NAME = "config.json";


int main(int argc, char **argv) {
    try {
        auto path = (Path(argv[0]).remove_filename() / CONFIG_FILE_NAME).generic_string();
        std::string corpus;
        File(path.c_str(), "rb").load_file(corpus);
        auto config = json::parse(corpus);

        SysCounters counters;
        auto metrics = config["metrics"];
        for (const auto& m : metrics) {
            counters.addMetrics(m["name"].get<std::string>(), m["counter"].get<std::string>(), m["kind"].get<std::string>());
        }

        // uncomment next row for testing your own metrics only
        //counters.traceMetrics(10, 1);
        //return 0;

        auto brokers = config["brokers"].get<std::string>();
        auto topic = config["topic"].get<std::string>();
        auto period = config["period"].get<int>();
        config.clear();
        corpus.clear();

        App app;
        app.create(brokers.c_str(), topic.c_str());
        printf("Run. \nbrokers: %s\ntopic: %s\nperiod: %d\n\nPress Ctrl-C to exit\n",
            brokers.c_str(), topic.c_str(), period);
        app.run(counters, period);
        app.flush(10000);
        return 0;
    } catch (std::exception error) {
        printf("Internal application error: %s\n", error.what());
        return 1;
    }
}
