package vendor.huawei.hardware.eid.V1_0;

import android.os.HidlSupport;
import android.os.HwBlob;
import android.os.HwParcel;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;

public final class INFO_SIGN_OUTPUT_INFO_L {
    public int infoLen;
    public final byte[] signInfo = new byte[BUFF_LEN_E.SEC_IMAGE_MAX_LEN];

    public final boolean equals(Object otherObject) {
        if (this == otherObject) {
            return true;
        }
        if (otherObject == null || otherObject.getClass() != INFO_SIGN_OUTPUT_INFO_L.class) {
            return false;
        }
        INFO_SIGN_OUTPUT_INFO_L other = (INFO_SIGN_OUTPUT_INFO_L) otherObject;
        if (this.infoLen == other.infoLen && HidlSupport.deepEquals(this.signInfo, other.signInfo)) {
            return true;
        }
        return false;
    }

    public final int hashCode() {
        return Objects.hash(new Object[]{Integer.valueOf(HidlSupport.deepHashCode(Integer.valueOf(this.infoLen))), Integer.valueOf(HidlSupport.deepHashCode(this.signInfo))});
    }

    public final String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("{");
        builder.append(".infoLen = ");
        builder.append(this.infoLen);
        builder.append(", .signInfo = ");
        builder.append(Arrays.toString(this.signInfo));
        builder.append("}");
        return builder.toString();
    }

    public final void readFromParcel(HwParcel parcel) {
        readEmbeddedFromParcel(parcel, parcel.readBuffer(491524), 0);
    }

    public static final ArrayList<INFO_SIGN_OUTPUT_INFO_L> readVectorFromParcel(HwParcel parcel) {
        ArrayList<INFO_SIGN_OUTPUT_INFO_L> _hidl_vec = new ArrayList();
        HwBlob _hidl_blob = parcel.readBuffer(16);
        int _hidl_vec_size = _hidl_blob.getInt32(8);
        HwBlob childBlob = parcel.readEmbeddedBuffer((long) (_hidl_vec_size * 491524), _hidl_blob.handle(), 0, true);
        _hidl_vec.clear();
        for (int _hidl_index_0 = 0; _hidl_index_0 < _hidl_vec_size; _hidl_index_0++) {
            INFO_SIGN_OUTPUT_INFO_L _hidl_vec_element = new INFO_SIGN_OUTPUT_INFO_L();
            _hidl_vec_element.readEmbeddedFromParcel(parcel, childBlob, (long) (_hidl_index_0 * 491524));
            _hidl_vec.add(_hidl_vec_element);
        }
        return _hidl_vec;
    }

    public final void readEmbeddedFromParcel(HwParcel parcel, HwBlob _hidl_blob, long _hidl_offset) {
        this.infoLen = _hidl_blob.getInt32(0 + _hidl_offset);
        _hidl_blob.copyToInt8Array(4 + _hidl_offset, this.signInfo, BUFF_LEN_E.SEC_IMAGE_MAX_LEN);
    }

    public final void writeToParcel(HwParcel parcel) {
        HwBlob _hidl_blob = new HwBlob(491524);
        writeEmbeddedToBlob(_hidl_blob, 0);
        parcel.writeBuffer(_hidl_blob);
    }

    public static final void writeVectorToParcel(HwParcel parcel, ArrayList<INFO_SIGN_OUTPUT_INFO_L> _hidl_vec) {
        HwBlob _hidl_blob = new HwBlob(16);
        int _hidl_vec_size = _hidl_vec.size();
        _hidl_blob.putInt32(8, _hidl_vec_size);
        int _hidl_index_0 = 0;
        _hidl_blob.putBool(12, false);
        HwBlob childBlob = new HwBlob(_hidl_vec_size * 491524);
        while (_hidl_index_0 < _hidl_vec_size) {
            ((INFO_SIGN_OUTPUT_INFO_L) _hidl_vec.get(_hidl_index_0)).writeEmbeddedToBlob(childBlob, (long) (_hidl_index_0 * 491524));
            _hidl_index_0++;
        }
        _hidl_blob.putBlob(0, childBlob);
        parcel.writeBuffer(_hidl_blob);
    }

    public final void writeEmbeddedToBlob(HwBlob _hidl_blob, long _hidl_offset) {
        _hidl_blob.putInt32(0 + _hidl_offset, this.infoLen);
        _hidl_blob.putInt8Array(4 + _hidl_offset, this.signInfo);
    }
}
