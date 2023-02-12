/**
 * Autogenerated by Avro
 *
 * DO NOT EDIT DIRECTLY
 */
package org.shaft.administration.kafka.avro.model;

import org.apache.avro.generic.GenericArray;
import org.apache.avro.specific.SpecificData;
import org.apache.avro.util.Utf8;
import org.apache.avro.message.BinaryMessageEncoder;
import org.apache.avro.message.BinaryMessageDecoder;
import org.apache.avro.message.SchemaStore;

@org.apache.avro.specific.AvroGenerated
public class EventAvroModel extends org.apache.avro.specific.SpecificRecordBase implements org.apache.avro.specific.SpecificRecord {
  private static final long serialVersionUID = 3519458277842379547L;


  public static final org.apache.avro.Schema SCHEMA$ = new org.apache.avro.Schema.Parser().parse("{\"type\":\"record\",\"name\":\"EventAvroModel\",\"namespace\":\"org.shaft.administration.kafka.avro.model\",\"fields\":[{\"name\":\"e\",\"type\":{\"type\":\"record\",\"name\":\"EventData\",\"fields\":[{\"name\":\"eid\",\"type\":\"long\"},{\"name\":\"quantity\",\"type\":\"long\"},{\"name\":\"name\",\"type\":{\"type\":\"string\",\"avro.java.string\":\"String\"}},{\"name\":\"costPrice\",\"type\":\"long\"},{\"name\":\"onSale\",\"type\":\"boolean\"},{\"name\":\"id\",\"type\":{\"type\":\"string\",\"avro.java.string\":\"String\"}},{\"name\":\"category\",\"type\":{\"type\":\"string\",\"avro.java.string\":\"String\"}},{\"name\":\"fp\",\"type\":{\"type\":\"string\",\"avro.java.string\":\"String\"}},{\"name\":\"option\",\"type\":{\"type\":\"string\",\"avro.java.string\":\"String\"}},{\"name\":\"ts\",\"type\":\"long\"}]}},{\"name\":\"i\",\"type\":{\"type\":\"string\",\"avro.java.string\":\"String\"}}]}");
  public static org.apache.avro.Schema getClassSchema() { return SCHEMA$; }

  private static final SpecificData MODEL$ = new SpecificData();

  private static final BinaryMessageEncoder<EventAvroModel> ENCODER =
      new BinaryMessageEncoder<>(MODEL$, SCHEMA$);

  private static final BinaryMessageDecoder<EventAvroModel> DECODER =
      new BinaryMessageDecoder<>(MODEL$, SCHEMA$);

  /**
   * Return the BinaryMessageEncoder instance used by this class.
   * @return the message encoder used by this class
   */
  public static BinaryMessageEncoder<EventAvroModel> getEncoder() {
    return ENCODER;
  }

  /**
   * Return the BinaryMessageDecoder instance used by this class.
   * @return the message decoder used by this class
   */
  public static BinaryMessageDecoder<EventAvroModel> getDecoder() {
    return DECODER;
  }

  /**
   * Create a new BinaryMessageDecoder instance for this class that uses the specified {@link SchemaStore}.
   * @param resolver a {@link SchemaStore} used to find schemas by fingerprint
   * @return a BinaryMessageDecoder instance for this class backed by the given SchemaStore
   */
  public static BinaryMessageDecoder<EventAvroModel> createDecoder(SchemaStore resolver) {
    return new BinaryMessageDecoder<>(MODEL$, SCHEMA$, resolver);
  }

  /**
   * Serializes this EventAvroModel to a ByteBuffer.
   * @return a buffer holding the serialized data for this instance
   * @throws java.io.IOException if this instance could not be serialized
   */
  public java.nio.ByteBuffer toByteBuffer() throws java.io.IOException {
    return ENCODER.encode(this);
  }

  /**
   * Deserializes a EventAvroModel from a ByteBuffer.
   * @param b a byte buffer holding serialized data for an instance of this class
   * @return a EventAvroModel instance decoded from the given buffer
   * @throws java.io.IOException if the given bytes could not be deserialized into an instance of this class
   */
  public static EventAvroModel fromByteBuffer(
      java.nio.ByteBuffer b) throws java.io.IOException {
    return DECODER.decode(b);
  }

  private org.shaft.administration.kafka.avro.model.EventData e;
  private java.lang.String i;

  /**
   * Default constructor.  Note that this does not initialize fields
   * to their default values from the schema.  If that is desired then
   * one should use <code>newBuilder()</code>.
   */
  public EventAvroModel() {}

  /**
   * All-args constructor.
   * @param e The new value for e
   * @param i The new value for i
   */
  public EventAvroModel(org.shaft.administration.kafka.avro.model.EventData e, java.lang.String i) {
    this.e = e;
    this.i = i;
  }

  @Override
  public org.apache.avro.specific.SpecificData getSpecificData() { return MODEL$; }

  @Override
  public org.apache.avro.Schema getSchema() { return SCHEMA$; }

  // Used by DatumWriter.  Applications should not call.
  @Override
  public java.lang.Object get(int field$) {
    switch (field$) {
    case 0: return e;
    case 1: return i;
    default: throw new IndexOutOfBoundsException("Invalid index: " + field$);
    }
  }

  // Used by DatumReader.  Applications should not call.
  @Override
  @SuppressWarnings(value="unchecked")
  public void put(int field$, java.lang.Object value$) {
    switch (field$) {
    case 0: e = (org.shaft.administration.kafka.avro.model.EventData)value$; break;
    case 1: i = value$ != null ? value$.toString() : null; break;
    default: throw new IndexOutOfBoundsException("Invalid index: " + field$);
    }
  }

  /**
   * Gets the value of the 'e' field.
   * @return The value of the 'e' field.
   */
  public org.shaft.administration.kafka.avro.model.EventData getE() {
    return e;
  }


  /**
   * Sets the value of the 'e' field.
   * @param value the value to set.
   */
  public void setE(org.shaft.administration.kafka.avro.model.EventData value) {
    this.e = value;
  }

  /**
   * Gets the value of the 'i' field.
   * @return The value of the 'i' field.
   */
  public java.lang.String getI() {
    return i;
  }


  /**
   * Sets the value of the 'i' field.
   * @param value the value to set.
   */
  public void setI(java.lang.String value) {
    this.i = value;
  }

  /**
   * Creates a new EventAvroModel RecordBuilder.
   * @return A new EventAvroModel RecordBuilder
   */
  public static org.shaft.administration.kafka.avro.model.EventAvroModel.Builder newBuilder() {
    return new org.shaft.administration.kafka.avro.model.EventAvroModel.Builder();
  }

  /**
   * Creates a new EventAvroModel RecordBuilder by copying an existing Builder.
   * @param other The existing builder to copy.
   * @return A new EventAvroModel RecordBuilder
   */
  public static org.shaft.administration.kafka.avro.model.EventAvroModel.Builder newBuilder(org.shaft.administration.kafka.avro.model.EventAvroModel.Builder other) {
    if (other == null) {
      return new org.shaft.administration.kafka.avro.model.EventAvroModel.Builder();
    } else {
      return new org.shaft.administration.kafka.avro.model.EventAvroModel.Builder(other);
    }
  }

  /**
   * Creates a new EventAvroModel RecordBuilder by copying an existing EventAvroModel instance.
   * @param other The existing instance to copy.
   * @return A new EventAvroModel RecordBuilder
   */
  public static org.shaft.administration.kafka.avro.model.EventAvroModel.Builder newBuilder(org.shaft.administration.kafka.avro.model.EventAvroModel other) {
    if (other == null) {
      return new org.shaft.administration.kafka.avro.model.EventAvroModel.Builder();
    } else {
      return new org.shaft.administration.kafka.avro.model.EventAvroModel.Builder(other);
    }
  }

  /**
   * RecordBuilder for EventAvroModel instances.
   */
  @org.apache.avro.specific.AvroGenerated
  public static class Builder extends org.apache.avro.specific.SpecificRecordBuilderBase<EventAvroModel>
    implements org.apache.avro.data.RecordBuilder<EventAvroModel> {

    private org.shaft.administration.kafka.avro.model.EventData e;
    private org.shaft.administration.kafka.avro.model.EventData.Builder eBuilder;
    private java.lang.String i;

    /** Creates a new Builder */
    private Builder() {
      super(SCHEMA$, MODEL$);
    }

    /**
     * Creates a Builder by copying an existing Builder.
     * @param other The existing Builder to copy.
     */
    private Builder(org.shaft.administration.kafka.avro.model.EventAvroModel.Builder other) {
      super(other);
      if (isValidValue(fields()[0], other.e)) {
        this.e = data().deepCopy(fields()[0].schema(), other.e);
        fieldSetFlags()[0] = other.fieldSetFlags()[0];
      }
      if (other.hasEBuilder()) {
        this.eBuilder = org.shaft.administration.kafka.avro.model.EventData.newBuilder(other.getEBuilder());
      }
      if (isValidValue(fields()[1], other.i)) {
        this.i = data().deepCopy(fields()[1].schema(), other.i);
        fieldSetFlags()[1] = other.fieldSetFlags()[1];
      }
    }

    /**
     * Creates a Builder by copying an existing EventAvroModel instance
     * @param other The existing instance to copy.
     */
    private Builder(org.shaft.administration.kafka.avro.model.EventAvroModel other) {
      super(SCHEMA$, MODEL$);
      if (isValidValue(fields()[0], other.e)) {
        this.e = data().deepCopy(fields()[0].schema(), other.e);
        fieldSetFlags()[0] = true;
      }
      this.eBuilder = null;
      if (isValidValue(fields()[1], other.i)) {
        this.i = data().deepCopy(fields()[1].schema(), other.i);
        fieldSetFlags()[1] = true;
      }
    }

    /**
      * Gets the value of the 'e' field.
      * @return The value.
      */
    public org.shaft.administration.kafka.avro.model.EventData getE() {
      return e;
    }


    /**
      * Sets the value of the 'e' field.
      * @param value The value of 'e'.
      * @return This builder.
      */
    public org.shaft.administration.kafka.avro.model.EventAvroModel.Builder setE(org.shaft.administration.kafka.avro.model.EventData value) {
      validate(fields()[0], value);
      this.eBuilder = null;
      this.e = value;
      fieldSetFlags()[0] = true;
      return this;
    }

    /**
      * Checks whether the 'e' field has been set.
      * @return True if the 'e' field has been set, false otherwise.
      */
    public boolean hasE() {
      return fieldSetFlags()[0];
    }

    /**
     * Gets the Builder instance for the 'e' field and creates one if it doesn't exist yet.
     * @return This builder.
     */
    public org.shaft.administration.kafka.avro.model.EventData.Builder getEBuilder() {
      if (eBuilder == null) {
        if (hasE()) {
          setEBuilder(org.shaft.administration.kafka.avro.model.EventData.newBuilder(e));
        } else {
          setEBuilder(org.shaft.administration.kafka.avro.model.EventData.newBuilder());
        }
      }
      return eBuilder;
    }

    /**
     * Sets the Builder instance for the 'e' field
     * @param value The builder instance that must be set.
     * @return This builder.
     */

    public org.shaft.administration.kafka.avro.model.EventAvroModel.Builder setEBuilder(org.shaft.administration.kafka.avro.model.EventData.Builder value) {
      clearE();
      eBuilder = value;
      return this;
    }

    /**
     * Checks whether the 'e' field has an active Builder instance
     * @return True if the 'e' field has an active Builder instance
     */
    public boolean hasEBuilder() {
      return eBuilder != null;
    }

    /**
      * Clears the value of the 'e' field.
      * @return This builder.
      */
    public org.shaft.administration.kafka.avro.model.EventAvroModel.Builder clearE() {
      e = null;
      eBuilder = null;
      fieldSetFlags()[0] = false;
      return this;
    }

    /**
      * Gets the value of the 'i' field.
      * @return The value.
      */
    public java.lang.String getI() {
      return i;
    }


    /**
      * Sets the value of the 'i' field.
      * @param value The value of 'i'.
      * @return This builder.
      */
    public org.shaft.administration.kafka.avro.model.EventAvroModel.Builder setI(java.lang.String value) {
      validate(fields()[1], value);
      this.i = value;
      fieldSetFlags()[1] = true;
      return this;
    }

    /**
      * Checks whether the 'i' field has been set.
      * @return True if the 'i' field has been set, false otherwise.
      */
    public boolean hasI() {
      return fieldSetFlags()[1];
    }


    /**
      * Clears the value of the 'i' field.
      * @return This builder.
      */
    public org.shaft.administration.kafka.avro.model.EventAvroModel.Builder clearI() {
      i = null;
      fieldSetFlags()[1] = false;
      return this;
    }

    @Override
    @SuppressWarnings("unchecked")
    public EventAvroModel build() {
      try {
        EventAvroModel record = new EventAvroModel();
        if (eBuilder != null) {
          try {
            record.e = this.eBuilder.build();
          } catch (org.apache.avro.AvroMissingFieldException e) {
            e.addParentField(record.getSchema().getField("e"));
            throw e;
          }
        } else {
          record.e = fieldSetFlags()[0] ? this.e : (org.shaft.administration.kafka.avro.model.EventData) defaultValue(fields()[0]);
        }
        record.i = fieldSetFlags()[1] ? this.i : (java.lang.String) defaultValue(fields()[1]);
        return record;
      } catch (org.apache.avro.AvroMissingFieldException e) {
        throw e;
      } catch (java.lang.Exception e) {
        throw new org.apache.avro.AvroRuntimeException(e);
      }
    }
  }

  @SuppressWarnings("unchecked")
  private static final org.apache.avro.io.DatumWriter<EventAvroModel>
    WRITER$ = (org.apache.avro.io.DatumWriter<EventAvroModel>)MODEL$.createDatumWriter(SCHEMA$);

  @Override public void writeExternal(java.io.ObjectOutput out)
    throws java.io.IOException {
    WRITER$.write(this, SpecificData.getEncoder(out));
  }

  @SuppressWarnings("unchecked")
  private static final org.apache.avro.io.DatumReader<EventAvroModel>
    READER$ = (org.apache.avro.io.DatumReader<EventAvroModel>)MODEL$.createDatumReader(SCHEMA$);

  @Override public void readExternal(java.io.ObjectInput in)
    throws java.io.IOException {
    READER$.read(this, SpecificData.getDecoder(in));
  }

  @Override protected boolean hasCustomCoders() { return true; }

  @Override public void customEncode(org.apache.avro.io.Encoder out)
    throws java.io.IOException
  {
    this.e.customEncode(out);

    out.writeString(this.i);

  }

  @Override public void customDecode(org.apache.avro.io.ResolvingDecoder in)
    throws java.io.IOException
  {
    org.apache.avro.Schema.Field[] fieldOrder = in.readFieldOrderIfDiff();
    if (fieldOrder == null) {
      if (this.e == null) {
        this.e = new org.shaft.administration.kafka.avro.model.EventData();
      }
      this.e.customDecode(in);

      this.i = in.readString();

    } else {
      for (int i = 0; i < 2; i++) {
        switch (fieldOrder[i].pos()) {
        case 0:
          if (this.e == null) {
            this.e = new org.shaft.administration.kafka.avro.model.EventData();
          }
          this.e.customDecode(in);
          break;

        case 1:
          this.i = in.readString();
          break;

        default:
          throw new java.io.IOException("Corrupt ResolvingDecoder.");
        }
      }
    }
  }
}










