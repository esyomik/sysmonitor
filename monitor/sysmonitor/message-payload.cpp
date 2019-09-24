#include "message-payload.hpp"


MessagePayload::MessagePayload() {}


MessagePayload::~MessagePayload() {}


const void* MessagePayload::data() const {
    return buffer_.data();
}


size_t MessagePayload::length() const {
    return buffer_.length();
}


void MessagePayload::reserve(size_t size) {
    buffer_.reserve(buffer_.size() + size);
}


void MessagePayload::clear() {
    buffer_.clear();
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


MessagePayload& MessagePayload::add(double value) {
    char buf[32];
    sprintf_s(buf, sizeof(buf), "%g;", value);
    buffer_ += buf;
    return *this;
}
