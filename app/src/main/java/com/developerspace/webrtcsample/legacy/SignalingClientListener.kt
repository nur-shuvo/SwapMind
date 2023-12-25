package com.developerspace.webrtcsample.legacy

import org.webrtc.IceCandidate
import org.webrtc.SessionDescription

interface SignalingClientListener {
    fun onConnectionEstablished()
    fun onOfferReceived(description: SessionDescription)
    fun onAnswerReceived(description: SessionDescription)
    fun onIceCandidateReceived(iceCandidate: IceCandidate)
    fun onCallEnded()
}