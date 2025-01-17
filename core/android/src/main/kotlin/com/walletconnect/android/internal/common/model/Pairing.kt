package com.walletconnect.android.internal.common.model

import com.walletconnect.android.internal.common.model.type.Sequence
import com.walletconnect.android.internal.utils.currentTimeInSeconds
import com.walletconnect.android.internal.utils.fiveMinutesInSeconds
import com.walletconnect.android.pairing.model.inactivePairing
import com.walletconnect.foundation.common.model.Topic

data class Pairing(
    override val topic: Topic,
    override val expiry: Expiry,
    val peerAppMetaData: AppMetaData? = null,
    val relayProtocol: String,
    val relayData: String?,
    val uri: String,
    val registeredMethods: String,
    val isProposalReceived: Boolean = false,
) : Sequence {
    val isActive: Boolean
        get() = (expiry.seconds - currentTimeInSeconds) > fiveMinutesInSeconds

    constructor(topic: Topic, relay: RelayProtocolOptions, symmetricKey: SymmetricKey, registeredMethods: String, expiry: Expiry) : this(
        topic = topic,
        expiry = expiry,
        relayProtocol = relay.protocol,
        relayData = relay.data,
        uri = WalletConnectUri(topic, symmetricKey, relay, expiry = expiry).toAbsoluteString(),
        registeredMethods = registeredMethods
    )

    constructor(uri: WalletConnectUri, registeredMethods: String) : this(
        topic = uri.topic,
        expiry = uri.expiry ?: Expiry(inactivePairing),
        relayProtocol = uri.relay.protocol,
        relayData = uri.relay.data,
        uri = uri.toAbsoluteString(),
        registeredMethods = registeredMethods
    )
}