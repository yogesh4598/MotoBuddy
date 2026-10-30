<?php
require_once __DIR__ . "/../../../config/db.php";

$data = json_decode(file_get_contents("php://input"));
$userMessage = isset($data->message) ? trim($data->message) : "";
$motoModel = isset($data->motorcycleModel) ? trim($data->motorcycleModel) : "Motorcycle";

if (empty($userMessage)) {
    http_response_code(400);
    echo json_encode(["success" => false, "message" => "Message cannot be empty"]);
    exit();
}

$reply = "";
$isWarning = false;

$lower = strtolower($userMessage);
if (strpos($lower, 'brake') !== false || strpos($lower, 'stop') !== false) {
    $reply = "⚠️ SAFETY WARNING: Spongy brake lever or squealing noise indicates worn pads or low brake fluid. Do NOT ride if braking power is reduced!";
    $isWarning = true;
} else if (strpos($lower, 'start') !== false || strpos($lower, 'click') !== false || strpos($lower, 'battery') !== false) {
    $reply = "Starting Diagnostics for $motoModel: Rapid clicking indicates low battery voltage. Check battery terminals, kill switch, and side stand sensor.";
} else if (strpos($lower, 'heat') !== false || strpos($lower, 'smoke') !== false || strpos($lower, 'hot') !== false) {
    $reply = "⚠️ OVERHEATING ALERT: Pull over safely and switch off engine immediately. Allow 20+ minutes cooling before inspecting coolant or oil levels.";
    $isWarning = true;
} else if (strpos($lower, 'chain') !== false) {
    $reply = "Drive Chain Maintenance: Adjust chain slack to 25-35 mm. Clean and lube every 500 km using O-ring safe lube.";
} else if (strpos($lower, 'oil') !== false) {
    $reply = "Engine Oil Guidance: Inspect oil level sight glass with bike held upright. Top up with 15W-50 Semi/Full Synthetic oil if below MIN level.";
} else {
    $reply = "MotoBuddy Diagnostic Engine: Is your $motoModel experiencing an issue related to starting, battery, engine overheating, unusual noises, or braking?";
}

echo json_encode([
    "success" => true,
    "message" => "AI Mechanic Response",
    "data" => [
        "reply" => $reply,
        "isWarning" => $isWarning
    ]
]);
?>
