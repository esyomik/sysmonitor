#pragma once

#include <string>

class MessagePayload {
    friend class App;

public:
    MessagePayload();
    ~MessagePayload();

    void reserve(size_t size);
    MessagePayload& add(const char* name, const char* value);
    MessagePayload& add(const char* name, double value);

private:
    std::string buffer_;

};

