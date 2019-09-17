#pragma once

#include <string>

class MessagePayload
{
public:
    MessagePayload();
    ~MessagePayload();

    const void* data() const;
    size_t length() const;

    void reserve(size_t size);
    void clear();
    MessagePayload& add(const char* name, const char* value);
    MessagePayload& add(const char* name, double value);
    MessagePayload& add(double value);

private:
    std::string buffer_;

};

