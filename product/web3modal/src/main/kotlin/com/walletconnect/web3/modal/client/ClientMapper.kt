package com.walletconnect.web3.modal.client

import com.walletconnect.sign.client.Sign
import com.walletconnect.web3.modal.client.models.Account
import com.walletconnect.web3.modal.client.models.Session
import com.walletconnect.web3.modal.client.models.request.Request

internal fun Sign.Model.ApprovedSession.toModal() = Modal.Model.ApprovedSession.WalletConnectSession(topic, metaData, namespaces.toModal(), accounts)

internal fun Map<String, Sign.Model.Namespace.Session>.toModal() =
    mapValues { (_, namespace) -> Modal.Model.Namespace.Session(namespace.chains, namespace.accounts, namespace.methods, namespace.events) }

internal fun Sign.Model.RejectedSession.toModal() = Modal.Model.RejectedSession(topic, reason)

internal fun Sign.Model.UpdatedSession.toModal() = Modal.Model.UpdatedSession(topic, namespaces.toModal())

internal fun Sign.Model.SessionEvent.toModal() = Modal.Model.SessionEvent(name, data)

internal fun Sign.Model.Session.toModal() = Modal.Model.Session(pairingTopic, topic, expiry, namespaces.toModal(), metaData)

internal fun Sign.Model.DeletedSession.toModal() = when (this) {
    is Sign.Model.DeletedSession.Error -> Modal.Model.DeletedSession.Error(error)
    is Sign.Model.DeletedSession.Success -> Modal.Model.DeletedSession.Success(topic, reason)
}

internal fun Sign.Model.SessionRequestResponse.toModal() = Modal.Model.SessionRequestResponse(topic, chainId, method, result.toModal())

internal fun Sign.Model.JsonRpcResponse.toModal() = when (this) {
    is Sign.Model.JsonRpcResponse.JsonRpcError -> Modal.Model.JsonRpcResponse.JsonRpcError(id, code, message)
    is Sign.Model.JsonRpcResponse.JsonRpcResult -> Modal.Model.JsonRpcResponse.JsonRpcResult(id, result)
}

@JvmSynthetic
internal fun Sign.Model.ExpiredProposal.toModal(): Modal.Model.ExpiredProposal = Modal.Model.ExpiredProposal(pairingTopic, proposerPublicKey)

@JvmSynthetic
internal fun Sign.Model.ExpiredRequest.toModal(): Modal.Model.ExpiredRequest = Modal.Model.ExpiredRequest(topic, id)

internal fun Sign.Model.ConnectionState.toModal() = Modal.Model.ConnectionState(isAvailable)

internal fun Sign.Model.Error.toModal() = Modal.Model.Error(throwable)

internal fun Sign.Params.Disconnect.toModal() = Modal.Params.Disconnect(sessionTopic)

internal fun Sign.Model.SentRequest.toModal() = Modal.Model.SentRequest(requestId, sessionTopic, method, params, chainId)

internal fun Sign.Model.Ping.Success.toModal() = Modal.Model.Ping.Success(topic)

internal fun Sign.Model.Ping.Error.toModal() = Modal.Model.Ping.Error(error)

// toSign()
internal fun Modal.Params.Connect.toSign() = Sign.Params.Connect(namespaces?.toSign(), optionalNamespaces?.toSign(), properties, pairing)

internal fun Map<String, Modal.Model.Namespace.Proposal>.toSign() = mapValues { (_, namespace) -> Sign.Model.Namespace.Proposal(namespace.chains, namespace.methods, namespace.events) }

internal fun Modal.Params.Disconnect.toSign() = Sign.Params.Disconnect(sessionTopic)

internal fun Modal.Params.Ping.toSign() = Sign.Params.Ping(topic)

internal fun Request.toSign(sessionTopic: String, chainId: String) = Sign.Params.Request(sessionTopic, method, params, chainId, expiry)

internal fun Modal.Listeners.SessionPing.toSign() = object : Sign.Listeners.SessionPing {
    override fun onSuccess(pingSuccess: Sign.Model.Ping.Success) {
        this@toSign.onSuccess(pingSuccess.toModal())
    }

    override fun onError(pingError: Sign.Model.Ping.Error) {
        this@toSign.onError(pingError.toModal())
    }
}

internal fun Sign.Model.Session.toSession() = Session.WalletConnectSession(pairingTopic, topic, expiry, namespaces.toModal(), metaData)

internal fun Account.toCoinbaseSession() = Session.CoinbaseSession(chain.id, address)

//internal fun Sign.Model.Session.toSession() = Session(pairingTopic, topic, expiry, namespaces.toModal(), metaData)
