package com.jasongermaine.revoluttest.core;

import org.immutables.value.Value;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;

@Value.Immutable
@Value.Style(typeAbstract = {"Abstract*", "*IF"}, typeImmutable = "*")
@JsonSerialize
public abstract class AbstractAccountCreationRequest implements AccountFields {
}
