#include "message-payload.hpp"


MessagePayload::MessagePayload() {}


MessagePayload::~MessagePayload() {}


void MessagePayload::reserve(size_t size) {
    buffer_.reserve(buffer_.size() + size);
}


MessagePayload& MessagePayload::add(const char* name, const char* value) {
    buffer_ += name;
    buffer_ += "=";
    buffer_ += value;
    buffer_ += ";";
    return *this;
}


MessagePayload& MessagePayload::add(const char* name, double value) {
    char buf[32];
    sprintf_s(buf, sizeof(buf), "=%g;", value);
    buffer_ += name;
    buffer_ += buf;
    return *this;
}

